package us.kbase.templates;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

public class TemplateFormatter {

    public static boolean formatTemplate(String templateName, Map<?,?> context, 
            boolean newStyle, File output) throws IOException {
        FileWriter fw = new FileWriter(output);
        try {
            return formatTemplate(templateName, context, newStyle, fw);
        } finally {
            fw.close();
        }
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static boolean formatTemplate(String templateName, Map<?,?> context, 
            boolean newStyle, Writer output) {
        try {
            if (!context.containsKey("esc")) {
                Map untyped = (Map)context;
                untyped.put("esc", new VelocityEscaper());
            }
            Reader input = new InputStreamReader(TemplateFormatter.class.getResourceAsStream(
                    templateName + "." + (newStyle ? "vm" : "old") + ".properties"), Charset.forName("utf-8"));
            VelocityContext cntx = new VelocityContext(context);
            boolean ret = Velocity.evaluate(cntx, output, "Template " + templateName, input);
            input.close();
            output.flush();
            return ret;
        } catch (Exception ex) {
            throw new IllegalStateException("Problems with template evaluation (" + templateName + ")", ex);
        }
    }
    
    public static class VelocityEscaper {
        public String print(String text) {
            return text;
        }
    }
}
