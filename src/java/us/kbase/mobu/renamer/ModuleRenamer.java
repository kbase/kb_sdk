package us.kbase.mobu.renamer;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.yaml.snakeyaml.Yaml;

import us.kbase.mobu.util.DirUtils;
import us.kbase.mobu.util.TextUtils;

public class ModuleRenamer {
    private final File moduleDir;
    private final Map<String,Object> kbaseYmlConfig;
    private final String oldModuleName;

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
        File kbaseYmlFile = new File(moduleDir, "kbase.yml");
        String origKbaseYml = TextUtils.readFileText(kbaseYmlFile);
        String newKbaseYml = replace(origKbaseYml, "module-name:\\s*(" + oldModuleName + ")", 
                newModuleName, "module-name key is not found in " + kbaseYmlFile);
        changes.add(new ChangeEvent(kbaseYmlFile, origKbaseYml, newKbaseYml));
        System.out.println(newKbaseYml);
        return changes;
    }

    public String replace(String text, String pattern, String changeTo,
            String error) {
        Pattern p1 = Pattern.compile(".*" + pattern + "(\\s.*)?", Pattern.DOTALL);
        Matcher m1 = p1.matcher(text);
        if (m1.matches()) {
            int start = m1.start(1);
            int end = m1.end(1);
            text = (start > 0 ? text.substring(0, start) : "") + changeTo + 
                    (end < text.length() ? text.substring(end) : "");
        } else {
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
