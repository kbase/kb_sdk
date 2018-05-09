package us.kbase.mobu;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import us.kbase.mobu.util.DirUtils;
import us.kbase.mobu.util.TextUtils;

/**
 * Fetch the information for a module that's getting built:
 * - name
 * - base directory
 * - work directory (scratch)
 */
public class ModuleInfo {

    public final Path moduleDir;
    public final Path workDir;
    public final String moduleName;

    /**
     * Instantiate ModuleInfo
     * Fetch the name from kbase.yml
     * Fetch the module directory using DirUtils
     */
    public ModuleInfo() throws IOException {
        moduleDir = DirUtils.findModuleDir().toPath();
        final Map<String, Object> config = loadYaml();
        moduleName = (String) config.get("module-name");
        workDir = Paths.get(moduleDir.toString(), "test_local", "workdir");
    }

    private Map<String, Object> loadYaml() throws IOException {
        final Yaml yaml = new Yaml();
        final Path yamlPath = Paths.get(moduleDir.toString(), "kbase.yml");
        InputStream input = Files.newInputStream(yamlPath);
        final Map<String, Object> config;
        try {
            config = (Map<String, Object>) yaml.load(input);
        } catch (Exception e) {
            throw new IOException("Invalid YAML configuration file in 'kbase.yml'");
        }
        return config;
    }
}
