package us.kbase.mobu.tester;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.core.type.TypeReference;

import us.kbase.auth.AuthToken;
import us.kbase.auth.TokenFormatException;
import us.kbase.catalog.CatalogClient;
import us.kbase.catalog.ModuleInfo;
import us.kbase.catalog.ModuleVersionInfo;
import us.kbase.catalog.SelectModuleVersionParams;
import us.kbase.catalog.SelectOneModuleParams;
import us.kbase.common.service.JsonClientException;
import us.kbase.common.service.UObject;
import us.kbase.common.service.JsonServerServlet.RpcCallData;
import us.kbase.common.utils.ModuleMethod;
import us.kbase.mobu.tester.CallbackServerConfigBuilder.CallbackServerConfig;
import us.kbase.mobu.util.ProcessHelper;

public class SDKSubsequentCallRunner extends SubsequentCallRunner {
    private static final Set<String> asyncVersionTags = Collections.unmodifiableSet(
            new LinkedHashSet<String>(Arrays.asList("dev", "beta", "release")));

    private String jobId;
    private File runSubJobsSh;
    private File sharedScratchDir;
    private File jobDir;
    private File jobWorkDir;
    private String imageName;
    private String callbackUrl;
    
    
    public SDKSubsequentCallRunner(
            final AuthToken token,
            final CallbackServerConfig config,
            final UUID jobId,
            final ModuleMethod modmeth,
            final String serviceVer)
            throws IOException, JsonClientException, TokenFormatException {
        super(token, config, jobId, modmeth, serviceVer);
    }

    public SDKSubsequentCallRunner(File testLocalDir, String methodName, 
            String serviceVer, int callbackPort) throws Exception {
        super(null, null, null, null, null);
                
        File srcWorkDir = new File(testLocalDir, "workdir");
        this.sharedScratchDir = new File(srcWorkDir, "tmp");
        File subjobsDir = new File(testLocalDir, "subjobs");
        if (!subjobsDir.exists())
            subjobsDir.mkdirs();
        long pref = System.currentTimeMillis();
        String suff = imageName.replace(':', '_').replace('/', '_');
        for (;;pref++) {
            jobId = pref + "_" + suff;
            this.jobDir = new File(subjobsDir, jobId);
            if (!jobDir.exists())
                break;
        }
        this.jobDir.mkdirs();
        this.jobWorkDir = new File(jobDir, "workdir");
        this.jobWorkDir.mkdirs();
        runSubJobsSh = new File(testLocalDir, "run_subjob.sh");
        if (!runSubJobsSh.exists()) {
            PrintWriter pw = new PrintWriter(runSubJobsSh);
            try {
                pw.println("#!/bin/bash");
                boolean isMac = System.getProperty("os.name").toLowerCase().contains("mac");
                String dockerRunCmd = testLocalDir.getCanonicalPath() + "/run_docker.sh run " +
                        (isMac ? "" : "--user $(id -u) ") + "-v " + subjobsDir.getCanonicalPath() + 
                        "/$1/workdir:/kb/module/work -v " + sharedScratchDir.getCanonicalPath() +
                        ":/kb/module/work/tmp -e \"SDK_CALLBACK_URL=$3\" $2 async";
                pw.println(dockerRunCmd);
            } finally {
                pw.close();
            }
        }
        System.out.println();
        ProcessHelper.cmd("chmod", "+x", runSubJobsSh.getCanonicalPath()).exec(testLocalDir);
        this.callbackUrl = ModuleTester.getCallbackUrl(callbackPort);
        File srcTokenFile = new File(srcWorkDir, "token");
        File dstTokenFile = new File(jobWorkDir, "token");
        FileUtils.copyFile(srcTokenFile, dstTokenFile);
        File srcConfigPropsFile = new File(srcWorkDir, "config.properties");
        File dstConfigPropsFile = new File(jobWorkDir, "config.properties");
        FileUtils.copyFile(srcConfigPropsFile, dstConfigPropsFile);
    }
    
    public Map<String, Object> oldrun(RpcCallData rpcCallData) throws Exception {
        File inputJson = new File(jobWorkDir, "input.json");
        UObject.getMapper().writeValue(inputJson, rpcCallData);
        ProcessHelper.cmd("bash", runSubJobsSh.getCanonicalPath(), jobId, imageName, 
                callbackUrl).exec(jobDir);
        File outputJson = new File(jobWorkDir, "output.json");
        if (outputJson.exists()) {
            return UObject.getMapper().readValue(outputJson, new TypeReference<Map<String, Object>>() {});
        } else {
            String errorMessage = "Unknown server error (output data wasn't produced)";
            Map<String, Object> error = new LinkedHashMap<String, Object>();
            error.put("name", "JSONRPCError");
            error.put("code", -32601);
            error.put("message", errorMessage);
            error.put("error", errorMessage);
            Map<String, Object> jsonRpcResponse = new LinkedHashMap<String, Object>();
            jsonRpcResponse.put("version", "1.1");
            jsonRpcResponse.put("error", error);
            return jsonRpcResponse;
        }
    }

    @Override
    protected Path runModule(
            final UUID jobId,
            final Path inputFile,
            final CallbackServerConfig config,
            final String imageName,
            final String moduleName,
            final AuthToken token)
            throws IOException, InterruptedException {
        final Path runSubJobsSh = config.getWorkDir().toAbsolutePath()
                .resolve("run_subjob.sh");
        if (Files.notExists(runSubJobsSh)) {
            final boolean isMac = System.getProperty("os.name").toLowerCase()
                    .contains("mac");
            final String dockerRunCmd = config.getWorkDir().toAbsolutePath() +
                    "/run_docker.sh run " + (isMac ? "" : "--user $(id -u) ") +
                    "-v " + config.getWorkDir().resolve(SUBJOBSDIR)
                        .toAbsolutePath() + 
                    "/$1/" + WORKDIR + ":/kb/module/work -v " +
                    getSharedScratchDir(config).toAbsolutePath() +
                    ":/kb/module/work/tmp -e \"SDK_CALLBACK_URL=$3\" $2 async";
            Files.write(runSubJobsSh, Arrays.asList(
                    "#!/bin/bash",
                    dockerRunCmd
                    ), StandardCharsets.UTF_8);
            final Set<PosixFilePermission> perms =
                    Files.getPosixFilePermissions(runSubJobsSh);
            perms.add(PosixFilePermission.OWNER_EXECUTE);
            Files.setPosixFilePermissions(runSubJobsSh, perms);
            Files.write(runSubJobsSh, Arrays.asList(
                    "#!/bin/bash",
                    dockerRunCmd
                    ), StandardCharsets.UTF_8);
        }
        final Path jobWorkDir = getJobWorkDir(jobId, config, imageName)
                .toAbsolutePath();
        Files.write(jobWorkDir.resolve("token"),
                Arrays.asList(token.toString()), StandardCharsets.UTF_8);
        
        // jobid is wrong jobDir is wrong
        ProcessHelper.cmd("bash", runSubJobsSh.toString(),
                jobWorkDir.getParent().getFileName().toString(), imageName,
                config.getCallbackURL().toExternalForm())
                .exec(jobWorkDir.getParent().toFile());
        return jobWorkDir.resolve("output.json");
    }
}
