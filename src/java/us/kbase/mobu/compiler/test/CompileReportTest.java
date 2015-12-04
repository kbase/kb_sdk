package us.kbase.mobu.compiler.test;

import java.util.Arrays;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import us.kbase.mobu.compiler.RunCompileCommand;

public class CompileReportTest {
    
    @Test
    public void testLines() throws Exception {
        String methodName = "build_pangenome_with_orthomcl";
        String server = "" +
                "class PangenomeOrthomcl:\n" +
                "\n" +
                "    def " + methodName + "(self, ctx, params):\n" +
                "        #BEGIN " + methodName + "\n" +
                "        log = \"\"\n" + 
                "        #END " + methodName + "\n" +
                "        return [log]\n";
        Map<String, RunCompileCommand.FunctionPlace> pos = 
                RunCompileCommand.parsePrevCode(server, "#", Arrays.asList(
                methodName, "fake2"));
        Assert.assertEquals(1, pos.size());
        Assert.assertEquals(4, (int)pos.get(methodName).startLine);
        Assert.assertEquals(6, (int)pos.get(methodName).endLine);
    }
}
