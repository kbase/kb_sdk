package us.kbase.mobu.compiler.test;

import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import us.kbase.common.service.UObject;
import us.kbase.kidl.KbModule;
import us.kbase.kidl.KbService;
import us.kbase.kidl.KidlParser;
import us.kbase.mobu.compiler.report.CompilationReporter;
import us.kbase.mobu.compiler.report.Function;
import us.kbase.mobu.compiler.report.FunctionPlace;
import us.kbase.mobu.compiler.report.Report;

public class CompileReporterTest {
    
    @Test
    public void testLines() throws Exception {
        String moduleName = "PangenomeOrthomcl";
        String methodName = "build_pangenome_with_orthomcl";
        String server = "" +
                "class " + moduleName + ":\n" +
                "\n" +
                "    def " + methodName + "(self, ctx, params):\n" +
                "        #BEGIN " + methodName + "\n" +
                "        log = \"\"\n" + 
                "        #END " + methodName + "\n" +
                "        return [log]\n";
        String kidlSpec = "" +
        		"module " + moduleName + " {\n" +
                "    /* Test that! */\n" + 
        		"    typedef tuple<int,string> MyType;\n" +
                "    /* Super func! */" +
                "    funcdef " + methodName + " (int, string, list<string>, mapping<string, float>) \n" +
                "        returns (UnspecifiedObject, MyType);\n" +
                "};";
        Map<String, String> kidlSpecs = new LinkedHashMap<String, String>();
        List<KbService> services = KidlParser.parseSpec(KidlParser.parseSpecInt(new StringReader(kidlSpec), 
                null, null));
        KbModule module = services.get(0).getModules().get(0);
        Report rpt = CompilationReporter.createReport(kidlSpecs, "", "", moduleName, module, "/kb/dev_forgetter", 
                "#", server);
        //System.out.println(UObject.transformObjectToString(rpt));
        Map<String, FunctionPlace> pos = rpt.functionPlaces;
        Assert.assertEquals(1, pos.size());
        Assert.assertEquals(4, (int)pos.get(methodName).startLine);
        Assert.assertEquals(6, (int)pos.get(methodName).endLine);
        Function f = rpt.functions.get(methodName);
        Assert.assertEquals("Super func!", f.comment);
        Assert.assertEquals(4, f.input.size());
        Assert.assertEquals(2, f.output.size());
        Assert.assertEquals("Test that!", f.output.get(1).comment);
    }
}
