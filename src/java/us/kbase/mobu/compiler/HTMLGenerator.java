package us.kbase.mobu.compiler;

import j2html.tags.Tag;

import java.io.FileReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import us.kbase.kidl.KbModule;
import us.kbase.kidl.KbService;
import us.kbase.kidl.KidlParser;

public class HTMLGenerator {

	public HTMLGenerator() {}
	
	
	//TODO HTML fix exceptions for this method
	//TODO HTML jars: j2HTML licence & push
	//TODO HTML of included files
	public void generate(final KbModule mod) throws Exception {
		System.out.println(mod.getModuleComponents().get(0));
		Tag t = mod.accept(new HTMLGenVisitor());
		Files.write(Paths.get("temp_htmlgen.html"), Arrays.asList(t.render()),
				StandardCharsets.UTF_8);
		
	}
	
	public static void main(String[] args) throws Exception {
		String specfile = args[0];
//		specfile = "/home/crusherofheads/localgit/workspace_deluxe/workspace.sp?ec";
		specfile = "/home/crusherofheads/localgit/user_and_job_state/userandjobstate.spec";
		Reader specReader = new FileReader(specfile);
		List<KbService> services = KidlParser.parseSpec(
				KidlParser.parseSpecInt(specReader, 
				null, null));
		new HTMLGenerator().generate(services.get(0).getModules().get(0));
	}
}
