package us.kbase.mobu.tester;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.core.type.TypeReference;

import us.kbase.catalog.CatalogClient;
import us.kbase.catalog.ModuleInfo;
import us.kbase.catalog.ModuleVersionInfo;
import us.kbase.catalog.SelectModuleVersionParams;
import us.kbase.catalog.SelectOneModuleParams;
import us.kbase.common.service.UObject;
import us.kbase.common.service.JsonServerServlet.RpcCallData;
import us.kbase.mobu.util.DirUtils;
import us.kbase.mobu.util.ProcessHelper;

public class SubsequentCallRunner {
    private static final Set<String> asyncVersionTags = Collections.unmodifiableSet(
            new LinkedHashSet<String>(Arrays.asList("dev", "beta", "release")));

    private String jobId;
    private File moduleDir;
    private File testLocalDir;
    private File runSubJobsSh;
    private File sharedScratchDir;
    private File jobDir;
    private File jobWorkDir;
    private String imageName;
    private String callbackUrl;
    
    public SubsequentCallRunner(String methodName, String serviceVer,
            int callbackPort) throws Exception {
        this(DirUtils.findModuleDir(), methodName, serviceVer, callbackPort);
    }
    
    public SubsequentCallRunner(File moduleDir, String methodName, 
            String serviceVer, int callbackPort) throws Exception {
        this.moduleDir = moduleDir;
        this.testLocalDir = new File(this.moduleDir, "test_local");
        Properties props = new Properties();
        InputStream is = new FileInputStream(new File(testLocalDir, "test.cfg"));
        try {
            props.load(is);
        } finally {
            is.close();
        }
        String kbaseEndpoint = props.getProperty("kbase_endpoint");
        String catalogUrl = kbaseEndpoint + "/catalog";
        CatalogClient catClient = new CatalogClient(new URL(catalogUrl));
        String moduleName = methodName.substring(0, methodName.indexOf('.'));
        String imageVersion = serviceVer;
        ModuleVersionInfo mvi = null;
        if (imageVersion == null || asyncVersionTags.contains(imageVersion)) {
            ModuleInfo mi = catClient.getModuleInfo(new SelectOneModuleParams().withModuleName(moduleName));
            if (imageVersion == null) {
                mvi = mi.getRelease();
            } else if (imageVersion.equals("dev")) {
                mvi = mi.getDev();
            } else if (imageVersion.equals("beta")) {
                mvi = mi.getBeta();
            } else {
                mvi = mi.getRelease();
            }
            if (mvi == null)
                throw new IllegalStateException("Cannot extract " + imageVersion + " version for module: " + moduleName);
            imageVersion = mvi.getGitCommitHash();
        } else {
            try {
                mvi = catClient.getVersionInfo(new SelectModuleVersionParams()
                        .withModuleName(moduleName).withGitCommitHash(imageVersion));
            } catch (Exception ex) {
                throw new IllegalStateException("Error retrieving module version info about image " +
                        moduleName + " with version " + imageVersion, ex);
            }
        }
        imageName = mvi.getDockerImgName();
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
                String dockerRunCmd = "docker run -v " + subjobsDir.getCanonicalPath() + 
                        "/$1/workdir:/kb/module/work -v " + sharedScratchDir.getCanonicalPath() +
                        ":/kb/module/work/tmp -e \"SDK_CALLBACK_URL=$3\" $2 async";
                pw.println(dockerRunCmd);
            } finally {
                pw.close();
            }
        }
        System.out.println();
        ProcessHelper.cmd("chmod", "+x", runSubJobsSh.getCanonicalPath()).exec(testLocalDir);
        this.callbackUrl = ModuleTester.getCallbackUrl(testLocalDir, callbackPort);
        File srcTokenFile = new File(srcWorkDir, "token");
        File dstTokenFile = new File(jobWorkDir, "token");
        FileUtils.copyFile(srcTokenFile, dstTokenFile);
        File srcConfigPropsFile = new File(srcWorkDir, "config.properties");
        File dstConfigPropsFile = new File(jobWorkDir, "config.properties");
        FileUtils.copyFile(srcConfigPropsFile, dstConfigPropsFile);
    }
    
    public Map<String, Object> run(RpcCallData rpcCallData) throws Exception {
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
}
