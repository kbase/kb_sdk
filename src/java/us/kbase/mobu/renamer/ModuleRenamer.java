package us.kbase.mobu.renamer;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.Yaml;

import us.kbase.jkidl.FileIncludeProvider;
import us.kbase.jkidl.IncludeProvider;
import us.kbase.kidl.KbService;
import us.kbase.kidl.KidlParser;
import us.kbase.mobu.compiler.JavaData;
import us.kbase.mobu.compiler.JavaFunc;
import us.kbase.mobu.compiler.JavaModule;
import us.kbase.mobu.compiler.JavaTypeGenerator;
import us.kbase.mobu.compiler.PrevCodeParser;
import us.kbase.mobu.compiler.TemplateBasedGenerator;
import us.kbase.mobu.util.DirUtils;
import us.kbase.mobu.util.FileSaver;
import us.kbase.mobu.util.TextUtils;

public class ModuleRenamer {
    private final File moduleDir;
    private final Map<String,Object> kbaseYmlConfig;
    private final String oldModuleName;
    private final String language;

    public ModuleRenamer() throws Exception {
        this(null);
    }
    
    public ModuleRenamer(File dir) throws Exception {
        moduleDir = dir == null ? DirUtils.findModuleDir() : DirUtils.findModuleDir(dir);
        String kbaseYml = TextUtils.readFileText(new File(moduleDir, "kbase.yml"));
        @SuppressWarnings("unchecked")
        Map<String,Object> config = (Map<String, Object>)new Yaml().load(kbaseYml);
        kbaseYmlConfig = config;
        oldModuleName = (String)kbaseYmlConfig.get("module-name");
        language = (String)kbaseYmlConfig.get("service-language");
    }
    
    public void rename(String newModuleName) throws Exception {
        // Let's try to do it assuming simple case. If we fail then all changes 
        // should be rolled back.
        List<ChangeEvent> changes = collectChanges(newModuleName);
        try {
            applyChanges(changes);
        } catch (Exception ex) {
            System.err.println("Error applying changes for renaming: " + ex);
            rollbackChanges(changes);
        }
    }
    
    public List<ChangeEvent> collectChanges(String newModuleName) throws Exception {
        List<ChangeEvent> changes = new ArrayList<ChangeEvent>();
        // kbase.yml
        File kbaseYmlFile = new File(moduleDir, "kbase.yml");
        String origKbaseYml = TextUtils.readFileText(kbaseYmlFile);
        String newKbaseYml = replace(origKbaseYml, "module-name:\\s*(" + oldModuleName + ")", 
                newModuleName, "module-name key is not found in " + kbaseYmlFile);
        changes.add(new ChangeEvent(kbaseYmlFile, origKbaseYml, newKbaseYml));
        // Makefile
        File makefileFile = new File(moduleDir, "Makefile");
        String origMakefile = TextUtils.readFileText(makefileFile);
        String oldModuleLower = oldModuleName.toLowerCase();
        String newMakefile = replace(origMakefile, "SERVICE\\s*=\\s*(" + oldModuleLower + ")", 
                newModuleName.toLowerCase(), "SERVICE variable is not found in " + makefileFile);
        newMakefile = replace(newMakefile, "SERVICE_CAPS\\s*=\\s*(" + oldModuleName + ")", 
                newModuleName, "SERVICE_CAPS variable is not found in " + makefileFile);
        newMakefile = replace(newMakefile, "SPEC_FILE\\s*=\\s*(" + oldModuleName + ")\\.spec", 
                newModuleName, "SPEC_FILE variable is not found in " + makefileFile);
        newMakefile = replace(newMakefile, "URL\\s*=\\s*https://kbase.us/services/(" + oldModuleLower + ")", 
                newModuleName.toLowerCase(), "URL variable is not found in " + makefileFile);
        if (newMakefile.toLowerCase().contains(oldModuleLower)) {
            System.out.println("WARNING! Some non-standard occurances of old module name were " +
            		"detected in " + makefileFile + ". Please check and correct them manually.");
        }
        changes.add(new ChangeEvent(makefileFile, origMakefile, newMakefile));
        // KIDL .spec file
        File origSpecFile = new File(moduleDir, oldModuleName + ".spec");
        String origSpec = TextUtils.readFileText(origSpecFile);
        File newSpecFile = new File(moduleDir, newModuleName + ".spec");
        String newSpec = replace(origSpec, "module\\s*(" + oldModuleName + ")\\s*\\{", newModuleName, 
                "Module name is not found in " + origSpecFile);
        changes.add(new ChangeEvent(origSpecFile, newSpecFile, origSpec, newSpec));
        // Clients and Impl's
        JavaData origParsingData = JavaTypeGenerator.parseSpec(origSpecFile);
        JavaModule origModule = origParsingData.getModules().get(0);
        List<String> methodNames = new ArrayList<String>();
        for (JavaFunc func: origModule.getFuncs())
            methodNames.add(func.getOriginal().getName());
        File codeBaseDir = new File(moduleDir, "lib");
        String origPackageName = oldModuleName;
        String origClientFileName = null;
        String origServerFileName = null;
        String origImplFileName = null;
        String newClientFileName = null;
        String newServerFileName = null;
        String newImplFileName = null;
        String commentPrefix = "#";
        boolean withClassHeader = false;
        boolean genPerlServer = false;
        String perlClientName = null;
        String perlServerName = null;
        String perlImplName = null;
        String perlPsgiName = null;
        boolean genPythonServer = false;
        String pythonClientName = null;
        String pythonServerName = null;
        String pythonImplName = null; 
        boolean genRServer = false;
        String rClientName = null;
        String rServerName = null;
        String rImplName = null;
        if (language.equalsIgnoreCase("java")) {
            codeBaseDir = new File(codeBaseDir, "src");
            origPackageName = origModule.getModulePackage();
            origImplFileName = TextUtils.capitalize(oldModuleName) + "Server.java";
            origClientFileName = TextUtils.capitalize(oldModuleName) + "Client.java";
            newImplFileName = TextUtils.capitalize(newModuleName) + "Server.java";
            newClientFileName = TextUtils.capitalize(newModuleName) + "Client.java";
            commentPrefix = "//";
            withClassHeader = true;
        } else  {
            String ext = null;
            if (language.equalsIgnoreCase("python") || language.equalsIgnoreCase("py")) {
                ext = "py";
                withClassHeader = true;
                genPythonServer = true;
                pythonClientName = newModuleName + "." + newModuleName + "Client";
                pythonServerName = newModuleName + "." + newModuleName + "Server";
                pythonImplName = newModuleName + "." + newModuleName + "Impl";
            } else if (language.equalsIgnoreCase("perl") || language.equalsIgnoreCase("pl")) {
                ext = "pm";
                genPerlServer = true;
                perlClientName = newModuleName + "::" + newModuleName + "Client";
                perlServerName = newModuleName + "::" + newModuleName + "Server";
                perlImplName = newModuleName + "::" + newModuleName + "Impl";
                perlPsgiName = newModuleName + ".psgi";
            } else if (language.equalsIgnoreCase("r")) {
                ext = "r";
                genRServer = true;
                rClientName = newModuleName + "/" + newModuleName + "Client";
                rServerName = newModuleName + "/" + newModuleName + "Server";
                rImplName = newModuleName + "/" + newModuleName + "Impl";
            }
            origClientFileName = oldModuleName + "Client." + ext;
            origServerFileName = oldModuleName + "Server." + ext;
            origImplFileName = oldModuleName + "Impl." + ext;
            newClientFileName = newModuleName + "Client." + ext;
            newServerFileName = newModuleName + "Server." + ext;
            newImplFileName = newModuleName + "Impl." + ext;
        }
        File origImplFile = new File(new File(codeBaseDir, origPackageName), origImplFileName);
        if (!origImplFile.exists())
            throw new IllegalStateException("Implementation file not found: " + origImplFile);
        Map<String, String> prevCode = PrevCodeParser.parsePrevCode(origImplFile, commentPrefix, methodNames, withClassHeader);
        IncludeProvider ip = new FileIncludeProvider(moduleDir);
        List<KbService> newParsing = KbService.loadFromMap(KidlParser.parseSpecInt(new StringReader(newSpec), null, ip));
        final Map<String, StringWriter> generatedFiles = new LinkedHashMap<String, StringWriter>();
        FileSaver srcOut = new FileSaver() {
            @Override
            public Writer openWriter(String path) throws IOException {
                StringWriter ret = new StringWriter();
                generatedFiles.put(path, ret);
                return ret;
            }
            @Override
            public OutputStream openStream(String path) throws IOException {
                return null;
            }
            @Override
            public File getAsFileOrNull(String path) throws IOException {
                return null;
            }
        };
        if (language.equalsIgnoreCase("java")) {
            JavaTypeGenerator.processSpec(newParsing, srcOut, ".", true, null, null, null, null, null,
                    null, null, null, null, prevCode);
        } else {
            TemplateBasedGenerator.generate(newParsing, null, false, null, 
                    false, perlClientName, genPerlServer, perlServerName, perlImplName, perlPsgiName, 
                    false, pythonClientName, genPythonServer, pythonServerName, pythonImplName, 
                    false, rClientName, genRServer, rServerName, rImplName, false, true, ip, 
                    srcOut, null, null, false, null, null, 
                    null, null, prevCode);
        }
        for (String key : generatedFiles.keySet()) {
            File newFile = new File(codeBaseDir, key);
            File origFile = new File(new File(codeBaseDir, origPackageName), newFile.getName());
            if (newFile.getName().equals(newImplFileName)) {
                origFile = new File(new File(codeBaseDir, origPackageName), origImplFileName);
            } else if (newFile.getName().equals(newClientFileName)) {
                origFile = new File(new File(codeBaseDir, origPackageName), origClientFileName);
            } else if (newServerFileName != null &&
                    newFile.getName().equals(newServerFileName)) {
                origFile = new File(new File(codeBaseDir, origPackageName), origServerFileName);
            } else if (newFile.getName().endsWith(".psgi") && perlPsgiName != null) {
                origFile = new File(codeBaseDir, oldModuleName + ".psgi");
            }
            if (origFile.exists()) {
                String origContent = TextUtils.readFileText(origFile);
                String newContent = generatedFiles.get(key).toString();
                if (origFile.equals(newFile)) {
                    boolean isChanged = !origContent.trim().equals(newContent.trim());
                    if (isChanged)
                        changes.add(new ChangeEvent(origFile, origContent, newContent));
                } else {
                    changes.add(new ChangeEvent(origFile, newFile, origContent, newContent));
                }
            }
        }
        return changes;
    }

    public String replace(String text, String pattern, String changeTo,
            String error) {
        return replace(text, pattern, changeTo, error, true);
    }
    
    public String replace(String text, String pattern, String changeTo,
            String error, boolean required) {
        Pattern p1 = Pattern.compile(".*" + pattern + "(\\s.*)?", Pattern.DOTALL);
        Matcher m1 = p1.matcher(text);
        if (m1.matches()) {
            int start = m1.start(1);
            int end = m1.end(1);
            text = (start > 0 ? text.substring(0, start) : "") + changeTo + 
                    (end < text.length() ? text.substring(end) : "");
        } else if (required) {
            throw new IllegalStateException(error);
        }
        return text;
    }
    
    public void applyChanges(List<ChangeEvent> changes) throws Exception {
        for (ChangeEvent ce : changes) {
            ce.isStarted = true;
            if (ce.isDeletion()) {
                System.out.println("Deleting " + ce.origFile);
                ce.origFile.delete();
            } else if (ce.isFileMoving()) {
                System.out.println("Moving " + ce.origFile + " -> " + ce.newFile);
                FileUtils.write(ce.newFile, ce.newContent);
                ce.origFile.delete();
            } else if (ce.isNewFile()) {
                System.out.println("Creating " + ce.newFile);
                FileUtils.write(ce.newFile, ce.newContent);
            } else {
                System.out.println("Changing " + ce.origFile);
                FileUtils.write(ce.origFile, ce.newContent);
            }
        }
    }

    public void rollbackChanges(List<ChangeEvent> changes) throws Exception {
        for (ChangeEvent ce : changes) {
            if (!ce.isStarted)
                continue;
            try {
                if (ce.isDeletion()) {
                    System.out.println("Rolling back for deletion of " + ce.origFile);
                    FileUtils.write(ce.origFile, ce.origContent);
                } else if (ce.isFileMoving()) {
                    System.out.println("Rolling back for move " + ce.origFile + " -> " + ce.newFile);
                    FileUtils.write(ce.origFile, ce.origContent);
                    ce.newFile.delete();
                } else if (ce.isNewFile()) {
                    System.out.println("Rolling back for creation of " + ce.newFile);
                    ce.newFile.delete();
                } else {
                    System.out.println("Rolling back for changes in " + ce.origFile);
                    FileUtils.write(ce.origFile, ce.origContent);
                }
            } catch (Exception ex) {
                System.err.println("Error rolling change back: " + ex);
            }
        }
    }

    private static class ChangeEvent {
        File origFile;      // null for new files
        File newFile;       // is set for file moving, null for local changes and deletions
        String origContent; // null for new files
        String newContent;  // is set for local changes (and files moving), null for deletions
        boolean isStarted = false;

        ChangeEvent(File origFile, String origContent) {
            this(origFile, null, origContent, null);  // to delete
        }
        
        ChangeEvent(File origFile, String origContent, String newContent) {
            this(origFile, null,  origContent, newContent); // local change
        }
        
        ChangeEvent(File origFile, File newFile, String origContent, String newContent) {
            // In case of new file: origFile = null and origContent = null
            this.origFile = origFile;
            this.newFile = newFile;
            this.origContent = origContent;
            this.newContent = newContent;
        }
        
        boolean isNewFile() {
            return origContent == null;
        }
        
        boolean isDeletion() {
            return newContent == null;
        }
        
        boolean isFileMoving() {
            return newFile != null;
        }
    }
}
