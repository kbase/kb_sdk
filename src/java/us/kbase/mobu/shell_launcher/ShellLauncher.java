package us.kbase.mobu.shell_launcher;

import java.io.File;
import java.io.IOException;
import java.lang.InterruptedException;
import java.lang.ProcessBuilder;
import java.lang.Process;
import java.util.Map;
import java.nio.file.Paths;

import org.yaml.snakeyaml.Yaml;

import us.kbase.mobu.util.DirUtils;
import us.kbase.mobu.util.TextUtils;
import us.kbase.mobu.ModuleBuilder;

/**
 * Launches the bash shell in a running docker instance.
 */
public class ShellLauncher {

    private final File moduleDir;
    private final String moduleName;
    private final String workDir;

    /**
     * Set some basic configuration, such as module name and work directory
     */
    public ShellLauncher() throws IOException {
        moduleDir = DirUtils.findModuleDir();
        String kbaseYml = TextUtils.readFileText(new File(moduleDir, "kbase.yml"));
        @SuppressWarnings("unchecked")
        Map<String,Object> config = (Map<String, Object>)new Yaml().load(kbaseYml);
        moduleName = (String)config.get("module-name");
        workDir = Paths.get(moduleDir.toPath().toString(), "test_local", "workdir").toString();
    }

    /**
     * Execute the docker command to launch the shell interactively.
     */
    public void exec() throws IOException, InterruptedException {
        // This command looks like:
        // docker run
        //   --interactive
        //   --tty=true
        //   --volume=WORKDIR:/kb/module/work test/module:latest
        //   bash
        String[] command = {
            "docker", "run",
            "--interactive",
            "--tty=true",
            "--volume=" + workDir + ":/kb/module/work", // Mount scratch
            "test/" + moduleName.toLowerCase() + ":latest", // Image to run
            "bash" // Command to run
        };
        ProcessBuilder pb = new ProcessBuilder(command);
        // Redirecting input and output streams allows the process to be interactive
        pb.redirectInput(ProcessBuilder.Redirect.INHERIT);
        pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        Process proc = pb.start();
        proc.waitFor();
    }
}
