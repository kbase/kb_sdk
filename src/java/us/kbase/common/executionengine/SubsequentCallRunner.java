package us.kbase.common.executionengine;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.core.type.TypeReference;

import us.kbase.auth.AuthToken;
import us.kbase.catalog.CatalogClient;
import us.kbase.catalog.ModuleVersion;
import us.kbase.catalog.SelectModuleVersion;
import us.kbase.common.executionengine.CallbackServerConfigBuilder.CallbackServerConfig;
import us.kbase.common.service.JsonClientException;
import us.kbase.common.service.ServerException;
import us.kbase.common.service.UObject;
import us.kbase.common.service.JsonServerServlet.RpcCallData;

public abstract class SubsequentCallRunner {
    
    //TODO NJS_SDK move to common repo
    
    private static final String RELEASE = JobRunnerConstants.RELEASE;
    private static final Set<String> RELEASE_TAGS =
            JobRunnerConstants.RELEASE_TAGS;
    
    public static final String SUBJOBSDIR = "subjobs";
    public static final String WORKDIR = "workdir";
    public static final String TEMPDIR = "tmp";

    private final AuthToken token;
    private final String moduleName;
    private final UUID jobId;
    private final String imageName;
    private final ModuleRunVersion mrv;
    private CallbackServerConfig config;
    
    public SubsequentCallRunner(
            final AuthToken token,
            final CallbackServerConfig config,
            final UUID jobId,
            final ModuleMethod modmeth, 
            String serviceVer)
            throws IOException, JsonClientException {
        this.token = token;
        this.config = config;
        final CatalogClient catClient = new CatalogClient(
                config.getCatalogURL());
        //TODO is this needed?
        catClient.setIsInsecureHttpConnectionAllowed(true);
        this.moduleName = modmeth.getModule();
        this.jobId = jobId;
        if (serviceVer == null || serviceVer.isEmpty()) {
            serviceVer = RELEASE;
        }
        final ModuleVersion mv;
        try {
            mv = catClient.getModuleVersion(new SelectModuleVersion()
                .withModuleName(moduleName).withVersion(serviceVer));
        } catch (ServerException se) {
            throw new IllegalArgumentException(String.format(
                    "Error looking up module %s with version %s: %s",
                    moduleName, serviceVer, se.getLocalizedMessage()));
        }
        mrv = new ModuleRunVersion(
                new URL(mv.getGitUrl()), modmeth,
                mv.getGitCommitHash(), mv.getVersion(),
                RELEASE_TAGS.contains(serviceVer) ? serviceVer : null);
        imageName = mv.getDockerImgName();
        Files.createDirectories(getSharedScratchDir(config));
        final Path jobWorkDir = getJobWorkDir(jobId, config, imageName);
        Files.createDirectories(jobWorkDir);
        config.writeJobConfigToFile(jobWorkDir.resolve(
                JobRunnerConstants.JOB_CONFIG_FILE));
    }

    protected static Path getJobWorkDir(
            final UUID jobId,
            final CallbackServerConfig config,
            final String imageName) {
        final Path subjobsDir = config.getWorkDir().resolve(SUBJOBSDIR);
        final String suff = imageName.replace(':', '_').replace('/', '_');
        final String workdir = jobId.toString() +  "_" + suff;
        return subjobsDir.resolve(workdir).resolve(WORKDIR);
    }
    
    protected static Path getSharedScratchDir(
            final CallbackServerConfig config) {
        return config.getWorkDir().resolve(WORKDIR).resolve(TEMPDIR);
    }
    
    /**
     * @return the version information for the module to be run.
     */
    public ModuleRunVersion getModuleRunVersion() {
        return mrv;
    }
    
    public Map<String, Object> run(RpcCallData rpcCallData)
            throws IOException, InterruptedException {
        final Path inputFile = getJobWorkDir(jobId, config, imageName)
                .resolve("input.json");
        UObject.getMapper().writeValue(inputFile.toFile(), rpcCallData);
        final Path outputFile = runModule(jobId, inputFile, config,
                imageName, moduleName, token);
        if (Files.exists(outputFile)) {
            return UObject.getMapper().readValue(outputFile.toFile(),
                    new TypeReference<Map<String, Object>>() {});
        } else {
            final String errorMessage =
                    "Unknown server error (output data wasn't produced)";
            final Map<String, Object> error =
                    new LinkedHashMap<String, Object>();
            error.put("name", "JSONRPCError");
            error.put("code", -32601);
            error.put("message", errorMessage);
            error.put("error", errorMessage);
            final Map<String, Object> jsonRpcResponse =
                    new LinkedHashMap<String, Object>();
            jsonRpcResponse.put("version", "1.1");
            jsonRpcResponse.put("error", error);
            return jsonRpcResponse;
        }
    }

    protected abstract Path runModule(
            final UUID jobId,
            final Path inputFile,
            final CallbackServerConfig config,
            final String imageName,
            final String moduleName,
            final AuthToken token)
            throws IOException,
            InterruptedException;
}
