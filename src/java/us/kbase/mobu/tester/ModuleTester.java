package us.kbase.mobu.tester;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.Yaml;

import us.kbase.auth.AuthService;
import us.kbase.mobu.util.ProcessHelper;
import us.kbase.mobu.util.TextUtils;
import us.kbase.templates.TemplateFormatter;

public class ModuleTester {
    private File moduleDir;
    protected Map<String,Object> kbaseYmlConfig;
    private Map<String, Object> moduleContext;

    public ModuleTester() throws Exception {
        File dir = new File(".").getCanonicalFile();
        while (!isModuleDir(dir)) {
            dir = dir.getParentFile();
            if (dir == null)
                throw new IllegalStateException("You're currently not in module folder");
        }
        moduleDir = dir;
        String kbaseYml = TextUtils.readFileText(new File(moduleDir, "kbase.yml"));
        @SuppressWarnings("unchecked")
        Map<String,Object> config = (Map<String, Object>)new Yaml().load(kbaseYml);
        kbaseYmlConfig = config;
        moduleContext = new HashMap<String, Object>();
        moduleContext.put("module_name", config.get("module-name"));
        moduleContext.put("module_root_path", moduleDir.getAbsolutePath());
    }
    
    private static boolean isModuleDir(File dir) {
        return  new File(dir, "Dockerfile").exists() &&
                new File(dir, "Makefile").exists() &&
                new File(dir, "kbase.yml").exists() &&
                new File(dir, "lib").exists() &&
                new File(dir, "scripts").exists() &&
                new File(dir, "test").exists() &&
                new File(dir, "ui").exists();
    }
    
    private static void checkIgnoreLine(File f, String line) throws IOException {
        List<String> lines = new ArrayList<String>();
        if (f.exists())
            lines.addAll(FileUtils.readLines(f));
        if (!new HashSet<String>(lines).contains(line)) {
            System.out.println("Warning: file \"" + f.getName() + "\" doesn't contain \"" + line + "\" line, it will be added.");
            lines.add(line);
            FileUtils.writeLines(f, lines);
        }
    }
    
    public void runTests() throws Exception {
        String testLocal = "test_local";
        checkIgnoreLine(new File(moduleDir, ".gitignore"), testLocal);
        checkIgnoreLine(new File(moduleDir, ".dockerignore"), testLocal);
        File tlDir = new File(moduleDir, testLocal);
        File runTestsSh = new File(tlDir, "build_run_tests.sh");
        File runBashSh = new File(tlDir, "run_bash.sh");
        if (!tlDir.exists()) {
            tlDir.mkdir();
            TemplateFormatter.formatTemplate("module_readme_test_local", moduleContext, true, new File(tlDir, "readme.txt"));
            TemplateFormatter.formatTemplate("module_test_cfg", moduleContext, true, new File(tlDir, "test.cfg"));
            TemplateFormatter.formatTemplate("module_build_run_tests", moduleContext, true, runTestsSh);
            TemplateFormatter.formatTemplate("module_run_bash", moduleContext, true, runBashSh);
            System.out.println("Set KBase account credentials in test_local/test.cfg and then test again");
            return;
        }
        Properties props = new Properties();
        InputStream is = new FileInputStream(new File(tlDir, "test.cfg"));
        try {
            props.load(is);
        } finally {
            is.close();
        }
        String user = props.getProperty("test_user");
        String password = props.getProperty("test_password");
        if (user == null || user.trim().isEmpty() || password == null) {
            throw new IllegalStateException("Error: KBase account credentials are not set in test_local/test.cfg");
        }
        String token = AuthService.login(user, password).getTokenString();
        File workDir = new File(tlDir, "workdir");
        workDir.mkdir();
        File tokenFile = new File(workDir, "token");
        FileWriter fw = new FileWriter(tokenFile);
        try {
            fw.write(token);
        } finally {
            fw.close();
        }
        File configPropsFile = new File(workDir, "config.properties");
        PrintWriter pw = new PrintWriter(configPropsFile);
        try {
            pw.println("[global]");
            pw.println("job_service_url = " + props.getProperty("job_service_url"));
            pw.println("workspace_url = " + props.getProperty("workspace_url"));
            pw.println("shock_url = " + props.getProperty("shock_url"));
        } finally {
            pw.close();
        }
        ProcessHelper.cmd("chmod", "+x", runTestsSh.getCanonicalPath()).exec(tlDir);
        ProcessHelper.cmd("chmod", "+x", runBashSh.getCanonicalPath()).exec(tlDir);
        ProcessHelper.cmd("bash", runTestsSh.getCanonicalPath()).exec(tlDir);
    }
}
