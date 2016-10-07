package us.kbase.mobu.tester;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

import us.kbase.auth.AuthToken;
import us.kbase.common.executionengine.ModuleMethod;
import us.kbase.common.executionengine.SubsequentCallRunner;
import us.kbase.common.executionengine.CallbackServerConfigBuilder.CallbackServerConfig;
import us.kbase.common.service.JsonClientException;
import us.kbase.mobu.util.ProcessHelper;

public class SDKSubsequentCallRunner extends SubsequentCallRunner {
    
    public SDKSubsequentCallRunner(
            final AuthToken token,
            final CallbackServerConfig config,
            final UUID jobId,
            final ModuleMethod modmeth,
            final String serviceVer)
            throws IOException, JsonClientException {
        super(token, config, jobId, modmeth, serviceVer);
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
            final boolean isWin = System.getProperty("os.name").toLowerCase()
                    .contains("win");
            final String dockerRunCmd = config.getWorkDir().toAbsolutePath() +
                    "/run_docker.sh run --rm " + (isMac || isWin ? "" : "--user $(id -u) ") +
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
        }
        final Path jobWorkDir = getJobWorkDir(jobId, config, imageName)
                .toAbsolutePath();
        Files.write(jobWorkDir.resolve("token"),
                Arrays.asList(token.getToken()), StandardCharsets.UTF_8);
        
        ProcessHelper.cmd("bash", runSubJobsSh.toString(),
                jobWorkDir.getParent().getFileName().toString(), imageName,
                config.getCallbackURL().toExternalForm())
                .exec(jobWorkDir.getParent().toFile());
        return jobWorkDir.resolve("output.json");
    }
}
