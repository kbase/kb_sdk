package us.kbase.mobu.renamer;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.yaml.snakeyaml.Yaml;

import us.kbase.kidl.KbService;
import us.kbase.kidl.KidlParser;
import us.kbase.kidl.test.KidlTest;
import us.kbase.mobu.compiler.JavaData;
import us.kbase.mobu.compiler.JavaTypeGenerator;
import us.kbase.mobu.util.DirUtils;
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
        String newMakefile = replace(newModuleName, "SERVICE\\s*=\\s*(" + oldModuleLower + ")", 
                newModuleName.toLowerCase(), "SERVICE variable is not found in " + makefileFile);
        newMakefile = replace(newModuleName, "SERVICE_CAPS\\s*=\\s*(" + oldModuleName + ")", 
                newModuleName, "SERVICE_CAPS variable is not found in " + makefileFile);
        newMakefile = replace(newModuleName, "SPEC_FILE\\s*=\\s*(" + oldModuleName + ")\\.spec", 
                newModuleName, "SPEC_FILE variable is not found in " + makefileFile);
        newMakefile = replace(newModuleName, "URL\\s*=\\s*https://kbase.us/services/(" + oldModuleLower + ")", 
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
        // Clients and Impl's
        JavaData parsingData = JavaTypeGenerator.parseSpec(origSpecFile);
        File packageFolder = null;
        
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
        
    }

    public void rollbackChanges(List<ChangeEvent> changes) throws Exception {
        
    }

    public static void main(String[] args) throws Exception {
        List<ChangeEvent> changes = new ModuleRenamer(
                new File("/Users/rsutormin/Temp/k/ContigFilterPython")).collectChanges("new1");
    }
    
    private static class ChangeEvent {
        File origFile;      // null for new files
        File newFile;       // is set for file moving, null for local changes and deletions
        String origContent; // null for new files
        String newContent;  // is set for local changes (and files moving), null for deletions
        boolean isDone = false;

        ChangeEvent(File origFile, String origContent) {
            this(origFile, null, origContent, null);  // to delete
        }
        
        ChangeEvent(File origFile, String origContent, String newContent) {
            this(origFile, null,  origContent, newContent); // local change
        }
        
        ChangeEvent(File origFile, File newFile, String origContent, String newContent) {
            this.origFile = origFile;
            this.newFile = newFile;
            this.origContent = origContent;
            this.newContent = newContent;
        }
        
        boolean isDeletion() {
            return newContent == null;
        }
        
        boolean isFileMoving() {
            return newFile != null;
        }
    }
}
