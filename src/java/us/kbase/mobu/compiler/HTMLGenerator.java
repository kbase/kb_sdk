package us.kbase.mobu.compiler;

import j2html.tags.Tag;

import java.io.FileReader;
import java.io.Reader;
import java.util.List;

import us.kbase.kidl.KbModule;
import us.kbase.kidl.KbService;
import us.kbase.kidl.KidlParser;

public class HTMLGenerator {

	public HTMLGenerator() {}
	
	
	//TODO HTML jars: j2HTML licence & push
	//TODO HTML of included files
	public void generate(final KbModule mod) {
		Tag t = mod.accept(new HTMLGenVisitor());
		System.out.println(t.render());
		
	}
	
	public static void main(String[] args) throws Exception {
		String specfile = args[0];
		Reader specReader = new FileReader(specfile);
		List<KbService> services = KidlParser.parseSpec(
				KidlParser.parseSpecInt(specReader, 
				null, null));
		new HTMLGenerator().generate(services.get(0).getModules().get(0));
	}
}
