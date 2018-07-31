package us.kbase.mobu.tester;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import us.kbase.auth.AuthToken;
import us.kbase.catalog.ModuleVersion;
import us.kbase.common.executionengine.ModuleMethod;
import us.kbase.common.executionengine.SubsequentCallRunner;
import us.kbase.common.executionengine.CallbackServerConfigBuilder.CallbackServerConfig;
import us.kbase.common.service.JsonClientException;
import us.kbase.mobu.util.ProcessHelper;

public class SDKSubsequentCallRunner extends SubsequentCallRunner {
    
    private DockerMountPoints mounts;
    
    public SDKSubsequentCallRunner(
            final AuthToken token,
            final CallbackServerConfig config,
            final UUID jobId,
            final ModuleMethod modmeth,
            final String serviceVer,
            final DockerMountPoints mounts)
            throws IOException, JsonClientException {
        super(token, config, jobId, modmeth, serviceVer);
        if (mounts == null) {
            throw new NullPointerException("mounts");
        }
        this.mounts = mounts;
    }

    @Override
    protected ModuleVersion loadModuleVersion(ModuleMethod modmeth,
            String serviceVer) throws IOException, JsonClientException {
        if (isLocalModule(modmeth.getModule())) {
            return new ModuleVersion().withModuleName(modmeth.getModule())
                    .withGitUrl("http://localhost")
                    .withGitCommitHash("local-docker-image").withVersion("local");
        } else {
            return super.loadModuleVersion(modmeth, serviceVer);
        }
    }

    /**
     * This method is supposed to be overwritten in order to provide map with local images
     * by module name. This is designed this way because this method is used in 
     * constructor of super-class which is part of common code base shared with execution
     * engine.
     */
    protected Map<String, String> getLocalModuleNameToImage() {
        return null;
    }
    
    public boolean isLocalModule(String module) {
        Map<String, String> localModuleNameToImage = getLocalModuleNameToImage();
        return localModuleNameToImage != null && 
                localModuleNameToImage.containsKey(module);
    }
    
    @Override
    protected String getImageName(ModuleVersion mv) {
        if (isLocalModule(mv.getModuleName())) {
            return getLocalModuleNameToImage().get(mv.getModuleName());
        } else {
            return super.getImageName(mv);
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
            final String dockerRunCmd = config.getWorkDir().toAbsolutePath() +
                    "/run_docker.sh run --rm " + "-v " +
                     config.getWorkDir().resolve(SUBJOBSDIR).toAbsolutePath() +
                    "/$1/" + WORKDIR + ":/kb/module/work -v " +
                    getSharedScratchDir(config).toAbsolutePath() +
                    ":/kb/module/work/tmp $4 -e \"SDK_CALLBACK_URL=$3\" $2 async";
            Files.write(runSubJobsSh, Arrays.asList(
                    "#!/bin/bash",
                    dockerRunCmd
                    ), StandardCharsets.UTF_8);
            final Set<PosixFilePermission> perms =
                    Files.getPosixFilePermissions(runSubJobsSh);
            perms.add(PosixFilePermission.OWNER_EXECUTE);
            Files.setPosixFilePermissions(runSubJobsSh, perms);
        }
        final Path jobWorkDir = getJobWorkDir(jobId, config, imageName)
                .toAbsolutePath();
        Files.write(jobWorkDir.resolve("token"),
                Arrays.asList(token.getToken()), StandardCharsets.UTF_8);
        
        ProcessHelper.cmd("bash", runSubJobsSh.toString(),
                jobWorkDir.getParent().getFileName().toString(), imageName,
                config.getCallbackURL().toExternalForm(),
                mounts.getDockerCommand())
                .exec(jobWorkDir.getParent().toFile());
        return jobWorkDir.resolve("output.json");
    }
}
