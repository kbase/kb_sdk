package us.kbase.mobu.tester;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
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
import us.kbase.mobu.validator.ModuleValidator;
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
        moduleContext.put("module_name", kbaseYmlConfig.get("module-name"));
        moduleContext.put("module_root_path", moduleDir.getAbsolutePath());
        if (kbaseYmlConfig.get("data-version") != null) {
            moduleContext.put("data_version", kbaseYmlConfig.get("data-version"));
        }
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
    
    public void runTests(String methodStoreUrl, boolean skipValidation, boolean allowSyncMethods) throws Exception {
        if (skipValidation) {
            System.out.println("Validation step is skipped");
        } else {
            ModuleValidator mv = new ModuleValidator(Arrays.asList(moduleDir.getCanonicalPath()), 
                    false, methodStoreUrl, allowSyncMethods);
            int returnCode = mv.validateAll();
            if (returnCode!=0) {
                System.out.println("You can skip validation step using -s (or --skip_validation) flag");
                System.exit(returnCode);
            }
        }
        String testLocal = "test_local";
        checkIgnoreLine(new File(moduleDir, ".gitignore"), testLocal);
        checkIgnoreLine(new File(moduleDir, ".dockerignore"), testLocal);
        File tlDir = new File(moduleDir, testLocal);
        File runTestsSh = new File(tlDir, "run_tests.sh");
        File runBashSh = new File(tlDir, "run_bash.sh");
        if (!tlDir.exists()) {
            tlDir.mkdir();
            TemplateFormatter.formatTemplate("module_readme_test_local", moduleContext, true, new File(tlDir, "readme.txt"));
            TemplateFormatter.formatTemplate("module_test_cfg", moduleContext, true, new File(tlDir, "test.cfg"));
            TemplateFormatter.formatTemplate("module_run_tests", moduleContext, true, runTestsSh);
            TemplateFormatter.formatTemplate("module_run_bash", moduleContext, true, runBashSh);
            System.out.println("Set KBase account credentials in test_local/test.cfg and then test again");
            return;
        } else if (kbaseYmlConfig.get("data-version") != null) {
            File refDataDir = new File(tlDir, "refdata");
            if (!refDataDir.exists()) {
                TemplateFormatter.formatTemplate("module_run_tests", moduleContext, true, runTestsSh);
                refDataDir.mkdir();
            }
        }
        if (!runTestsSh.exists()) {
            TemplateFormatter.formatTemplate("module_run_tests", moduleContext, true, runTestsSh);
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
            pw.println("kbase_endpoint = " + props.getProperty("kbase_endpoint"));
        } finally {
            pw.close();
        }
        ProcessHelper.cmd("chmod", "+x", runBashSh.getCanonicalPath()).exec(tlDir);
        String moduleName = (String)kbaseYmlConfig.get("module-name");
        String imageName = "test/" + moduleName.toLowerCase() + ":latest";
        System.out.println();
        System.out.println("Delete old Docker containers");
        List<String> lines = exec(tlDir, "docker", "ps", "-a");
        for (String line : lines) {
            String[] parts = splitByWhiteSpaces(line);
            if (parts[1].equals(imageName)) {
                String cntId = parts[0];
                ProcessHelper.cmd("docker", "rm", "-v", "-f", cntId).exec(tlDir);
            }
        }
        String oldImageId = findImageIdByName(tlDir, imageName);    
        System.out.println();
        System.out.println("Build Docker image");
        boolean ok = buildImage(moduleDir, imageName);
        if (!ok)
            return;
        if (oldImageId != null) {
            String newImageId = findImageIdByName(tlDir, imageName);
            if (!newImageId.equals(oldImageId)) {  // It's not the same image (not all layers are cached)
                System.out.println("Delete old Docker image");
                ProcessHelper.cmd("docker", "rmi", oldImageId).exec(tlDir);
            }
        }
        if (!runTestsSh.exists()) {
            pw = new PrintWriter(runTestsSh);
            try {
                String dockerRunCmd = "docker run -v " + tlDir.getCanonicalPath() + "/workdir:" +
                        "/kb/module/work " + imageName + " test";
                pw.println(dockerRunCmd);
            } finally {
                pw.close();
            }
        }
        System.out.println();
        ProcessHelper.cmd("chmod", "+x", runTestsSh.getCanonicalPath()).exec(tlDir);
        ProcessHelper.cmd("bash", runTestsSh.getCanonicalPath()).exec(tlDir);
    }

    public String findImageIdByName(File tlDir, String imageName)
            throws Exception {
        List<String> lines;
        String ret = null;
        lines = exec(tlDir, "docker", "images");
        for (String line : lines) {
            String[] parts = splitByWhiteSpaces(line);
            String name = parts[0] + ":" + parts[1];
            if (name.equals(imageName)) {
                ret = parts[2];
                break;
            }
        }
        return ret;
    }

    public String[] splitByWhiteSpaces(String line) {
        String[] parts = line.split("\\s+");
        return parts;
    }
    
    private static List<String> exec(File workDir, String... cmd) throws Exception {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ProcessHelper.cmd(cmd).exec(workDir, null, pw, pw);
        pw.close();
        List<String> ret = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new StringReader(sw.toString()));
        while (true) {
            String l = br.readLine();
            if (l == null)
                break;
            ret.add(l);
        }
        br.close();
        return ret;
    }
    
    private boolean buildImage(File repoDir, String targetImageName) throws Exception {
        Process p = Runtime.getRuntime().exec(new String[] {"docker", "build", "--rm", "-t", 
                targetImageName, repoDir.getCanonicalPath()});
        List<Thread> workers = new ArrayList<Thread>();
        InputStream[] inputStreams = new InputStream[] {p.getInputStream(), p.getErrorStream()};
        final String[] cntIdToDelete = {null};
        final String[] imageIdToDelete = {null};
        for (int i = 0; i < inputStreams.length; i++) {
            final InputStream is = inputStreams[i];
            final boolean isError = i == 1;
            Thread ret = new Thread(new Runnable() {
                public void run() {
                    try {
                        BufferedReader br = new BufferedReader(new InputStreamReader(is));
                        while (true) {
                            String line = br.readLine();
                            if (line == null)
                                break;
                            if (isError) {
                                System.err.println(line);
                            } else {
                                System.out.println(line);
                                if (line.startsWith(" ---> Running in ")) {
                                    String[] parts = splitByWhiteSpaces(line.trim());
                                    if (parts.length > 3) {
                                        String cntId = parts[parts.length - 1];
                                        cntIdToDelete[0] = cntId;
                                    }
                                } else if (line.startsWith(" ---> ")) {
                                    String[] parts = splitByWhiteSpaces(line.trim());
                                    if (parts.length > 1) {
                                        String imageId = parts[parts.length - 1];
                                        imageIdToDelete[0] = imageId;
                                    }
                                }
                            }
                        }
                        br.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new IllegalStateException("Error reading data from executed container", e);
                    }
                }
            });
            ret.start();
            workers.add(ret);
        }
        for (Thread t : workers)
            t.join();
        p.waitFor();
        int exitCode = p.exitValue();
        if (exitCode != 0) {
            try {
                if (cntIdToDelete[0] != null) {
                    System.out.println("Cleaning up building container: " + cntIdToDelete[0]);
                    Thread.sleep(1000);
                    ProcessHelper.cmd("docker", "rm", "-v", "-f", cntIdToDelete[0]).exec(repoDir);
                }
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        }
        return exitCode == 0;
    }

}
