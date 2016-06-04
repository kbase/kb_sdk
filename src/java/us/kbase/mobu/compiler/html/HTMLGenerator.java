package us.kbase.mobu.compiler.html;

import static j2html.TagCreator.body;
import static j2html.TagCreator.document;
import static j2html.TagCreator.head;
import static j2html.TagCreator.html;
import static j2html.TagCreator.link;
import static j2html.TagCreator.title;

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
	
	
	//TODO HTML figure out what's going on with comment whitespace & fix
	//TODO HTML fix exceptions for this method
	//TODO HTML jars: j2HTML licence & push
	//TODO HTML of included files
	public void generate(final KbModule mod) throws Exception {
		Tag t = mod.accept(new HTMLGenVisitor());
		Tag page = html().with(
				head().with(
						title(mod.getModuleName()),
						link().withRel("stylesheet").withHref("temp_css.css")
				),
				body().with(t)
				);
				
		Files.write(Paths.get("temp_htmlgen.html"),
				Arrays.asList(document().render(), page.render()),
				StandardCharsets.UTF_8);
		
	}
	
	public static void main(String[] args) throws Exception {
		String specfile = args[0];
		specfile = "/home/crusherofheads/localgit/workspace_deluxe/workspace.spec";
//		specfile = "/home/crusherofheads/localgit/user_and_job_state/userandjobstate.spec";
		Reader specReader = new FileReader(specfile);
		List<KbService> services = KidlParser.parseSpec(
				KidlParser.parseSpecInt(specReader, 
				null, null));
		new HTMLGenerator().generate(services.get(0).getModules().get(0));
	}
}
