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

public class ShellLauncher {
    File moduleDir;
    String moduleName;
    String workDir;

    public ShellLauncher() throws IOException {
        moduleDir = DirUtils.findModuleDir();
        String kbaseYml = TextUtils.readFileText(new File(moduleDir, "kbase.yml"));
        @SuppressWarnings("unchecked")
        Map<String,Object> config = (Map<String, Object>)new Yaml().load(kbaseYml);
        moduleName = (String)config.get("module-name");
        workDir = Paths.get(moduleDir.toPath().toString(), "test_local", "workdir").toString();
    }

    public Process exec() throws IOException, InterruptedException {
        // Returns status code
        String[] command = {
            "docker", "run", "-i", "-t", "-v",
            workDir + ":/kb/module/work", // Mount scratch
            "test/" + moduleName.toLowerCase() + ":latest", // Image to run
            "bash" // Command to run
        };
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectInput(ProcessBuilder.Redirect.INHERIT);
        pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        Process proc = pb.start();
        proc.waitFor();
        return proc;
    }
}
