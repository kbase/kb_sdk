package us.kbase.templates;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import us.kbase.mobu.util.TextUtils;

public class TemplateFormatter {

    public static boolean formatTemplate(String templateName, Map<?,?> context, 
            File output) throws IOException {
        StringWriter sw = new StringWriter();
        boolean ret = formatTemplate(templateName, context, sw);
        sw.close();
        StringReader sr = new StringReader(sw.toString());
        TextUtils.writeFileLines(TextUtils.readReaderLines(sr, true), output);
        return ret;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static boolean formatTemplate(String templateName, Map<?,?> context, 
            Writer output) {
        try {
            if (!context.containsKey("esc")) {
                Map untyped = (Map)context;
                untyped.put("esc", new VelocityEscaper());
            }
            Reader input = new InputStreamReader(TemplateFormatter.class.getResourceAsStream(
                    templateName + "." + "vm.properties"), Charset.forName("utf-8"));
            VelocityContext cntx = new VelocityContext(context);
            boolean ret = Velocity.evaluate(cntx, output, "Template " + templateName, input);
            input.close();
            output.flush();
            return ret;
        } catch (Exception ex) {
            throw new IllegalStateException("Problems with template evaluation (" + templateName + ")", ex);
        }
    }
    
    public static InputStream getResource(final String resource) {
        return TemplateFormatter.class.getResourceAsStream(resource);
    }
    
    public static class VelocityEscaper {
        public String print(String text) {
            return text;
        }
    }
}
