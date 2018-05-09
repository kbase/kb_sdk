package us.kbase.mobu.shell_launcher;

import java.io.IOException;
import java.lang.InterruptedException;
import java.lang.ProcessBuilder;
import java.lang.Process;

import us.kbase.mobu.ModuleInfo;

/**
 * Launches the bash shell in a running docker instance.
 */
public class ShellLauncher {

    private final ModuleInfo moduleInfo;

    /**
     * Load the module information.
     */
    public ShellLauncher() throws IOException {
        moduleInfo = new ModuleInfo();
    }

    /**
     * Execute the docker command to launch the shell interactively.
     */
    public void exec() throws IOException, InterruptedException {
        // This command looks like:
        //   docker run \
        //     --interactive \
        //     --tty=true \
        //     --volume=WORKDIR:/kb/module/work test/module:latest \
        //     bash
        final String[] command = {
            "docker", "run", "--interactive", "--tty=true",
            "--volume=" + moduleInfo.workDir + ":/kb/module/work", // Mount scratch
            "test/" + moduleInfo.moduleName.toLowerCase() + ":latest", // Image to run
            "bash" // Command to run
        };
        final ProcessBuilder pb = new ProcessBuilder(command);
        // Redirecting input and output streams allows the process to be interactive
        pb.redirectInput(ProcessBuilder.Redirect.INHERIT);
        pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        final Process proc = pb.start();
        proc.waitFor();
    }
}
