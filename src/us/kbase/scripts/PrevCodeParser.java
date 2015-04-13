package us.kbase.scripts;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class PrevCodeParser {
    public static final String HEADER = "HEADER";
    public static final String CLSHEADER = "CLASS_HEADER";
    public static final String CONSTRUCTOR = "CONSTRUCTOR";
    public static final String METHOD = "METHOD_";
    
    public static HashMap<String, String> parsePrevCode(File implFile, String commentPrefix,
            List<String> funcs, boolean withClassHeader) throws IOException, ParseException {
        commentPrefix = Pattern.quote(commentPrefix);
        Pattern PAT_HEADER = Pattern.compile(".*" + commentPrefix + 
                "BEGIN_HEADER\n(.*\n)?[ \t]*" + commentPrefix + "END_HEADER\n.*", Pattern.DOTALL);
        Pattern PAT_CLASS_HEADER = Pattern.compile(".*" + commentPrefix + 
                "BEGIN_CLASS_HEADER\n(.*\n)?[ \t]*" + commentPrefix + "END_CLASS_HEADER\n.*", Pattern.DOTALL);
        Pattern PAT_CONSTRUCTOR = Pattern.compile(".*" + commentPrefix + "BEGIN_CONSTRUCTOR\n(.*\n)?[ \t]*" + 
                commentPrefix + "END_CONSTRUCTOR\n.*", Pattern.DOTALL);
        HashMap<String, String> code = new HashMap<String, String>();
        if (implFile == null || !implFile.exists()) {
            code.put(HEADER, "");
            if (withClassHeader)
                code.put(CLSHEADER, "");
            code.put(CONSTRUCTOR, "");
            return code;
        }
        String backupExtension = ".bak-" + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
        File backup = new File(implFile.getAbsoluteFile() + backupExtension);
        FileUtils.copyFile(implFile, backup);
        String oldserver = IOUtils.toString(new FileReader(implFile));
        checkMatch(code, PAT_HEADER, oldserver, HEADER, "header", true, implFile);
        if (withClassHeader)
            checkMatch(code, PAT_CLASS_HEADER, oldserver, CLSHEADER, "class header", true, implFile);
        checkMatch(code, PAT_CONSTRUCTOR, oldserver, CONSTRUCTOR, "constructor", true, implFile);
        for (String funcName : funcs) {
            Pattern p = Pattern.compile(MessageFormat.format(".*" + commentPrefix + "BEGIN {0}\n(.*\n)?[ \t]*" + 
                    commentPrefix + "END {0}\n.*", funcName), Pattern.DOTALL);
            checkMatch(code, p, oldserver, METHOD + funcName, "method " + funcName, false, implFile);
        }
        return code;
    }

    private static void checkMatch(HashMap<String, String> code, Pattern matcher,
            String oldserver, String codekey, String errortype, boolean exceptOnFail,
            File file) 
            throws ParseException {
        Matcher m = matcher.matcher(oldserver);
        if (!m.matches()) {
            if (exceptOnFail) {
                throw new ParseException("Missing " + errortype + 
                        " in original file [" + file + "]", 0);
            } else {
                return;
            }
        }
        String value = m.group(1);
        if (value == null)
            value = "";
        code.put(codekey, value);
    }
    
    public static void main(String[] args) throws Exception {
        String oldserver = "=cut\n" +
                "\n" +
                "#BEGIN_HEADER\n" +
                "vf\n" + 
                "   #END_HEADER\n" +
                "\n" +
                "sub new\n" +
                "{\n";
        String commentPrefix = Pattern.quote("#");
        Pattern PAT_HEADER = Pattern.compile(".*" + commentPrefix + 
                "BEGIN_HEADER\n(.*\n)?[ \t]*" + commentPrefix + "END_HEADER\n.*", Pattern.DOTALL);
        HashMap<String, String> code = new HashMap<String, String>();
        checkMatch(code, PAT_HEADER, oldserver, HEADER, "header", true, new File("./temp.txt"));
    }
}
