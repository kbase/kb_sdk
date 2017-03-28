package us.kbase.mobu.installer;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

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
        this(null, true);
    }

    public ClientInstaller(File dir, boolean showWarnings) throws Exception {
        moduleDir = dir == null ? DirUtils.findModuleDir() : DirUtils.findModuleDir(dir);
        String kbaseYml = TextUtils.readFileText(new File(moduleDir, "kbase.yml"));
        @SuppressWarnings("unchecked")
        Map<String,Object> config = (Map<String, Object>)new Yaml().load(kbaseYml);
        kbaseYmlConfig = config;
        language = (String)kbaseYmlConfig.get("service-language");
        File sdkCfgFile = new File(moduleDir, "sdk.cfg");
        sdkConfig = new Properties();
        if (!sdkCfgFile.exists()) {
            if(showWarnings) {
                System.out.println("Warning: file " + sdkCfgFile.getAbsolutePath() + " will be " +
                    "initialized (with 'catalog_url' parameter pointing to AppDev environment)");
            }
            FileUtils.writeLines(sdkCfgFile, Arrays.asList("catalog_url=" +
                    "https://appdev.kbase.us/services/catalog"));
            TextUtils.checkIgnoreLine(new File(moduleDir, ".gitignore"), sdkCfgFile.getName(), showWarnings);
            TextUtils.checkIgnoreLine(new File(moduleDir, ".dockerignore"), sdkCfgFile.getName(), showWarnings);
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

    public int install(String lang, boolean async, boolean core, boolean dynamic, String tagVer, 
            boolean verbose, String moduleName, String libDirName) throws Exception {
        return install(lang, async, core, dynamic, tagVer, verbose, moduleName, libDirName, null);
    }
    
    public int install(String lang, boolean async, boolean core, boolean dynamic, String tagVer, 
            boolean verbose, String moduleName, String libDirName, String clientName) 
                    throws Exception {
        if (core && (dynamic || async)) {
            throw new IllegalStateException("It's not allowed to set 'core' mode and one " +
                    "of 'async'/'dynamic' at the same time.");
        }
        FileProvider fp = null;
        String semanticVersion = null;
        String gitUrl = null;
        String gitCommitHash = null;
        String filePath = null;
        if (moduleName.contains("/") || moduleName.contains("\\")) {
            // This is the case for local spec-file processing (defined by file path or URL)
            filePath = moduleName;
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
            if (!(core || dynamic || async)) {
                dynamic = modVer.getDynamicService() != null && modVer.getDynamicService() == 1L;
                async = true;
            }
            if (modVer.getCompilationReport() == null)
                throw new IllegalStateException("Compilation report is not found for this " +
                		"version of [" + moduleName + "] module.");
            List<SpecFile> specFiles = modVer.getCompilationReport().getSpecFiles();
            if (specFiles == null)
                throw new IllegalStateException("Compilation report returned from catalog is " +
                		"out of date. [" + moduleName + "] module should be reregistered again.");
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
        if (async && dynamic) {
            compile(lang, async, false, tagVer, verbose, moduleName, libDirName, fp, 
                    semanticVersion, gitUrl, gitCommitHash, clientName, filePath);
            if (clientName == null) {
                clientName = moduleName;
            }
            compile(lang, false, dynamic, tagVer, verbose, moduleName, libDirName, fp, 
                    semanticVersion, gitUrl, gitCommitHash, clientName + "Service", filePath);
        } else {
            compile(lang, async, dynamic, tagVer, verbose, moduleName, libDirName, fp, 
                    semanticVersion, gitUrl, gitCommitHash, clientName, filePath);
        }
        return 0;
    }

    private void compile(String lang, boolean async, boolean dynamic, String tagVer, 
            boolean verbose, String moduleName, String libDirName, FileProvider fp, 
            String semanticVersion, String gitUrl, String gitCommitHash,
            String clientName, String filePath) throws Exception {
        String url = null;
        String clientAsyncVer = null;
        String dynservVer = null;
        if (dynamic) {
            url = "https://kbase.us/services/service_wizard";
            dynservVer = tagVer == null ? "release" : tagVer;
        } else if (async) {
            // We are getting rid of default URL in async case because of unexpected behavior
            // when for local calls callback URL is missed.
            //url = "https://kbase.us/services/njs_wrapper";
            clientAsyncVer = tagVer == null ? "release" : tagVer;
        }
        final FileProvider fp2 = fp;
        IncludeProvider ip = new IncludeProvider() {
            @Override
            public Map<String, KbModule> parseInclude(String includeLine) 
                    throws KidlParseException {
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
                    throw new KidlParseException("Error parsing spec-file [" + specFileName +
                            "]: " + e.getMessage());
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
        if (clientName == null) {
            clientName = moduleName;
        }
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
            String customClientClassName = TextUtils.capitalize(clientName) + "Client";
            URL urlEndpoint = url == null ? null : new URL(url);
            JavaTypeGenerator.processSpec(services, javaSrcDir, javaPackageParent, false, null, 
                    null, urlEndpoint, null, null, clientAsyncVer, dynservVer, semanticVersion, 
                    gitUrl, gitCommitHash, null, customClientClassName);
        } else {
            String perlClientName = null;
            if (isPerl)
                perlClientName = moduleName + "::" + clientName + "Client";
            String pyClientName = null;
            if (isPython)
                pyClientName = moduleName + "." + clientName + "Client";
            String rClientName = null;
            if (isR)
                rClientName = moduleName + "/" + clientName + "Client";
            String jsClientName = null;
            if (isJS)
                jsClientName = moduleName + "/" + clientName + "Client";
            FileSaver output = new DiskFileSaver(libDir);
            TemplateBasedGenerator.generate(services, url, isJS, jsClientName, isPerl, 
                    perlClientName, false, null, null, null, isPython, pyClientName, false, null,
                    null, isR, rClientName, false, null, null, false, ip, output, null, null, 
                    async, clientAsyncVer, dynservVer, semanticVersion, gitUrl, gitCommitHash);
        }
        // Now let's add record about this client to dependencies.json file
        boolean isSdk = dynamic || async;
        String versionTag = isSdk ? (tagVer == null ? "release" : tagVer) : null;
        FileSaver depsDir = new DiskFileSaver(moduleDir);
        addDependency(moduleName, isSdk, versionTag, filePath, depsDir);
    }
    
    public static void addDependency(String moduleName, boolean isSdk, String versionTag, 
            String filePath, FileSaver depsDir) throws Exception {
        Map<String, Dependency> depMap = new TreeMap<String, Dependency>();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        File depsFile = depsDir.getAsFileOrNull("dependencies.json");
        if (depsFile != null && depsFile.exists()) {
            try {
                List<Dependency> deps = mapper.readValue(depsFile, 
                        new TypeReference<List<Dependency>>() {});
                for (Dependency dep : deps) {
                    depMap.put(dep.moduleName.toLowerCase(), dep);
                }
            } catch (Exception ex) {
                throw new IllegalStateException("Error parsing depedencies file [" + depsFile +
                        "]: " + ex.getMessage(), ex);
            }
        }
        Dependency dep = new Dependency();
        dep.moduleName = moduleName;
        dep.type = isSdk ? "sdk" : "core";
        dep.versionTag = versionTag;
        dep.filePath = filePath;
        depMap.put(moduleName.toLowerCase(), dep);
        List<Dependency> deps = new ArrayList<Dependency>(depMap.values());
        try (Writer depsWr = depsDir.openWriter("dependencies.json")) {
            mapper.writeValue(depsWr, deps);
        }
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
