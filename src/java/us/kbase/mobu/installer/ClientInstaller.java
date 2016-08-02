package us.kbase.mobu.installer;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.yaml.snakeyaml.Yaml;

import us.kbase.catalog.CatalogClient;
import us.kbase.catalog.ModuleVersion;
import us.kbase.catalog.SelectModuleVersion;
import us.kbase.catalog.SpecFile;
import us.kbase.jkidl.IncludeProvider;
import us.kbase.jkidl.ParseException;
import us.kbase.jkidl.SpecParser;
import us.kbase.kidl.KbModule;
import us.kbase.kidl.KbService;
import us.kbase.kidl.KidlParseException;
import us.kbase.kidl.KidlParser;
import us.kbase.mobu.compiler.JavaTypeGenerator;
import us.kbase.mobu.compiler.TemplateBasedGenerator;
import us.kbase.mobu.util.DirUtils;
import us.kbase.mobu.util.DiskFileSaver;
import us.kbase.mobu.util.FileSaver;
import us.kbase.mobu.util.TextUtils;

public class ClientInstaller {
    private final File moduleDir;
    private final Map<String, Object> kbaseYmlConfig;
    private final String language;
    private final Properties sdkConfig;
    private final URL catalogUrl;
    
    public ClientInstaller() throws Exception {
        this(null);
    }

    public ClientInstaller(File dir) throws Exception {
        moduleDir = dir == null ? DirUtils.findModuleDir() : DirUtils.findModuleDir(dir);
        String kbaseYml = TextUtils.readFileText(new File(moduleDir, "kbase.yml"));
        @SuppressWarnings("unchecked")
        Map<String,Object> config = (Map<String, Object>)new Yaml().load(kbaseYml);
        kbaseYmlConfig = config;
        language = (String)kbaseYmlConfig.get("service-language");
        File sdkCfgFile = new File(moduleDir, "sdk.cfg");
        sdkConfig = new Properties();
        if (!sdkCfgFile.exists()) {
            System.out.println("Warning: file " + sdkCfgFile.getAbsolutePath() + " will be " +
            		"initialized (with 'catalog_url' parameter pointing to AppDev environment)");
            FileUtils.writeLines(sdkCfgFile, Arrays.asList("catalog_url=" +
            		"https://appdev.kbase.us/services/catalog"));
            TextUtils.checkIgnoreLine(new File(moduleDir, ".gitignore"), sdkCfgFile.getName());
            TextUtils.checkIgnoreLine(new File(moduleDir, ".dockerignore"), sdkCfgFile.getName());
        }
        InputStream is = new FileInputStream(sdkCfgFile);
        try {
            sdkConfig.load(is);
        } finally {
            is.close();
        }
        String catalogUrlText = sdkConfig.getProperty("catalog_url");
        if (catalogUrlText == null) {
            throw new IllegalStateException("Couldn't find 'catalog_url' parameter in " + sdkCfgFile);
        }
        catalogUrl = new URL(catalogUrlText);
    }
    
    public int install(String lang, boolean async, boolean sync, boolean dynamic, String tagVer, 
            boolean verbose, String moduleName, String libDirName) throws Exception {
        FileProvider fp = null;
        String url = null;
        String clientAsyncVer = null;
        String dynservVer = null;
        String semanticVersion = null;
        String gitUrl = null;
        String gitCommitHash = null;
        if (moduleName.contains("/") || moduleName.contains("\\")) {
            // This is the case for local spec-file processing (defined by file path or URL)
            if (isLocalFile(moduleName)) {
                final File specFile = new File(moduleName);
                final File dir = specFile.getParentFile();
                fp = new FileProvider() {
                    @Override
                    public String loadMainSpec() throws Exception {
                        return FileUtils.readFileToString(specFile);
                    }
                    @Override
                    public String loadIncludedSpec(String specFileName) throws Exception {
                        return FileUtils.readFileToString(new File(dir, specFileName));
                    }
                };
            } else if (isUrl(moduleName)) {
                final URL specUrl = new URL(moduleName);
                final URL parUrl = specUrl.toURI().resolve(".").toURL();
                fp = new FileProvider() {
                    @Override
                    public String loadMainSpec() throws Exception {
                        InputStream is = specUrl.openStream();
                        try {
                            return IOUtils.toString(is);
                        } finally {
                            is.close();
                        }
                    }
                    @Override
                    public String loadIncludedSpec(String specFileName) throws Exception {
                        InputStream is = parUrl.toURI().resolve(specFileName).toURL().openStream();
                        try {
                            return IOUtils.toString(is);
                        } finally {
                            is.close();
                        }
                    }
                };
            } else {
                throw new IllegalStateException("Path " + moduleName + " is not recognized as " +
                		"existing local file or URL");
            }
            moduleName = null;
        } else {
            CatalogClient client = new CatalogClient(catalogUrl);
            ModuleVersion modVer = client.getModuleVersion(
                    new SelectModuleVersion().withModuleName(moduleName).withVersion(tagVer)
                    .withIncludeCompilationReport(1L));
            gitUrl = modVer.getGitUrl();
            gitCommitHash = modVer.getGitCommitHash();
            semanticVersion = modVer.getVersion();
            if (!(dynamic || async || sync)) {
                dynamic = modVer.getDynamicService() != null && modVer.getDynamicService() == 1L;
                async = !dynamic;
            }
            if (modVer.getCompilationReport() == null)
                throw new IllegalStateException("Compilation report is not found for this version " +
                		"of [" + moduleName + "] module.");
            List<SpecFile> specFiles = modVer.getCompilationReport().getSpecFiles();
            if (specFiles == null)
                throw new IllegalStateException("Compilation report returned from catalog is out " +
                		"of date. [" + moduleName + "] module should be reregistered again.");
            final List<String> mainSpec = new ArrayList<String>();
            final Map<String, String> deps = new LinkedHashMap<String, String>();
            for (SpecFile spec : specFiles) {
                deps.put(spec.getFileName(), spec.getContent());
                if (spec.getIsMain() != null && (long)spec.getIsMain() != 0L)
                    mainSpec.add(spec.getContent());
            }
            fp = new FileProvider() {
                @Override
                public String loadMainSpec() throws Exception {
                    return mainSpec.get(0);
                }
                @Override
                public String loadIncludedSpec(String specFileName) throws Exception {
                    return deps.get(specFileName);
                }
            };
        }
        if (dynamic) {
            url = "https://kbase.us/services/service_wizard";
            dynservVer = tagVer == null ? "release" : tagVer;
        } else if (async) {
            url = "https://kbase.us/services/njs_wrapper";
            clientAsyncVer = tagVer == null ? "release" : tagVer;
        }
        final FileProvider fp2 = fp;
        IncludeProvider ip = new IncludeProvider() {
            @Override
            public Map<String, KbModule> parseInclude(String includeLine) throws KidlParseException {
                String specPath = includeLine.trim();
                if (specPath.startsWith("#include"))
                    specPath = specPath.substring(8).trim();
                if (specPath.startsWith("<"))
                    specPath = specPath.substring(1).trim();
                if (specPath.endsWith(">"))
                    specPath = specPath.substring(0, specPath.length() - 1).trim();
                String specFileName = new File(specPath).getName();
                try {
                    String specText = fp2.loadIncludedSpec(specFileName);
                    SpecParser p = new SpecParser(new StringReader(specText));
                    return p.SpecStatement(this);
                } catch (ParseException e) {
                    throw new KidlParseException("Error parsing spec-file [" + specFileName + "]: " + 
                            e.getMessage());
                } catch (Exception e) {
                    throw new IllegalStateException("Unexpected error", e);
                }
            }
        };
        List<KbService> services = KidlParser.parseSpec(KidlParser.parseSpecInt(
                new StringReader(fp.loadMainSpec()), null, ip));
        if (moduleName == null)
            for (KbService srv : services)
                for (KbModule md : srv.getModules())
                    moduleName = md.getModuleName();
        if (lang == null)
            lang = language;
        lang = lang.toLowerCase();
        boolean isPerl = false;
        boolean isPython = false;
        boolean isJava = false;
        boolean isR = false;
        boolean isJS = false;
        String[] perlNames = {"perl", ".pl", "pl"};
        if (Arrays.asList(perlNames).contains(lang)) {
            isPerl = true;
        } else {
            String[] pythonNames = {"python", ".py", "py"};
            if (Arrays.asList(pythonNames).contains(lang)) {
                isPython = true;
            } else {
                String[] javaNames = {"java", ".java"};
                if (Arrays.asList(javaNames).contains(lang)) {
                    isJava = true;
                } else {
                    String[] rNames = {"r", ".r"};
                    if (Arrays.asList(rNames).contains(lang)) {
                        isR = true;
                    } else {
                        String[] jsNames = {"js", ".js", "javascript"};
                        if (Arrays.asList(jsNames).contains(lang)) {
                            isJS = true;
                        }
                    }
                }
            }
        }
        File libDir = new File(moduleDir, libDirName == null ? "lib" : libDirName);
        if (isJava) {
            FileSaver javaSrcDir = new DiskFileSaver(new File(libDir, "src"));
            String javaPackageParent = ".";
            JavaTypeGenerator.processSpec(services, javaSrcDir, 
                    javaPackageParent, false, null, null, 
                    url == null ? null : new URL(url), null, null,
                    clientAsyncVer, dynservVer, semanticVersion, gitUrl, gitCommitHash);
        } else {
            String perlClientName = null;
            if (isPerl)
                perlClientName = moduleName + "::" + moduleName + "Client";
            String pyClientName = null;
            if (isPython)
                pyClientName = moduleName + "." + moduleName + "Client";
            String rClientName = null;
            if (isR)
                rClientName = moduleName + "/" + moduleName + "Client";
            String jsClientName = null;
            if (isJS)
                jsClientName = moduleName + "/" + moduleName + "Client";
            FileSaver output = new DiskFileSaver(libDir);
            TemplateBasedGenerator.generate(services, url, isJS, jsClientName, isPerl, perlClientName, 
                    false, null, null, null, isPython, pyClientName, false, null, null, isR, 
                    rClientName, false, null, null, false, true, ip, output, null, null, 
                    async, clientAsyncVer, dynservVer, semanticVersion, gitUrl, gitCommitHash);
        }
        return 0;
    }
    
    private static boolean isUrl(String url) {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }
    
    private static boolean isLocalFile(String path) {
        try {
            File f = new File(path);
            return f.exists() && f.isFile();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    private static interface FileProvider {
        public String loadMainSpec() throws Exception;
        public String loadIncludedSpec(String specFileName) throws Exception;
    }
}
