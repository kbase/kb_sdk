package us.kbase.mobu.validator.test;

import java.io.StringReader;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import us.kbase.kidl.KbModule;
import us.kbase.kidl.KbService;
import us.kbase.kidl.KidlParser;
import us.kbase.mobu.validator.ModuleValidator;

public class ModuleValidatorTest {
    
    @Test
    public void testSimpleParams() throws Exception {
        validateMapping(1);
    }

    @Test
    public void testGroupParams() throws Exception {
        validateMapping(2);
    }

    private static void validateMapping(int num) throws Exception {
        String kidlSpec = loadTextResource("kidl_" + num + ".properties");
        List<KbService> services = KidlParser.parseSpec(KidlParser.parseSpecInt(
                new StringReader(kidlSpec), null, null));
        KbModule parsedKidl = services.get(0).getModules().get(0);
        String methodSpec = loadTextResource("spec_" + num + ".properties");
        ModuleValidator.validateMethodSpecMapping(methodSpec, parsedKidl, false);
    }
    
    private static String loadTextResource(String name) throws Exception {
        return IOUtils.toString(ModuleValidatorTest.class.getResourceAsStream(name));
    }
}
