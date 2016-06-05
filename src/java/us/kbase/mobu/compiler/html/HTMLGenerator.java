package us.kbase.mobu.compiler.html;

import static j2html.TagCreator.body;
import static j2html.TagCreator.document;
import static j2html.TagCreator.head;
import static j2html.TagCreator.html;
import static j2html.TagCreator.link;
import static j2html.TagCreator.title;

import j2html.tags.Tag;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import us.kbase.jkidl.FileIncludeProvider;
import us.kbase.jkidl.IncludeProvider;
import us.kbase.jkidl.MemoizingIncludeProvider;
import us.kbase.kidl.KbModule;
import us.kbase.kidl.KidlParseException;
import us.kbase.kidl.KidlParser;
import us.kbase.mobu.util.DiskFileSaver;
import us.kbase.mobu.util.FileSaver;

public class HTMLGenerator {

	public HTMLGenerator() {}
	
	//TODO HTML javadoc
	//TODO HTML how handle warnings for bad links? Logger makes most sense
	//TODO HTML include CSS file
	//TODO HTML figure out what's going on with comment whitespace & fix
	//TODO HTML jars: j2HTML licence & push
	public void generate(
			final Reader spec,
			final IncludeProvider includes,
			final FileSaver saver)
			throws KidlParseException, IOException {
		final MemoizingIncludeProvider memo =
				new MemoizingIncludeProvider(includes);
		final KbModule main = KidlParser.parseSpec(
				KidlParser.parseSpecInt(spec, null, memo))
				.get(0).getModules().get(0);
		final Map<String, Res> modules = new HashMap<String, Res>();
		final HTMLGenVisitor visitor = new HTMLGenVisitor();
		modules.put(main.getModuleName(), new Res(main, visitor,
				main.accept(visitor)));
		for (final KbModule m: memo.getParsed().values()) {
			modules.put(m.getModuleName(), new Res(m, visitor,
					m.accept(visitor)));
		}
		//TODO HTML check for bad links
		for (final Res r: modules.values()) {
			
			writeHTML(saver, r);
		}
	}

	private void writeHTML(
			final FileSaver saver,
			final Res res) throws IOException {
		Tag page = html().with(
				head().with(
						title(res.mod.getModuleName()),
						link().withRel("stylesheet").withHref("temp_css.css")
				),
				body().with(res.html)
				);
		
		//TODO HTML generate include lines
		//TODO HTML typedef & funcdef indexes
		try (final Writer w = saver.openWriter(
				res.mod.getModuleName() + ".html")) {
			w.write(document().render());
			w.write(page.render());
		}
	}
	
	public static void main(String[] args) throws Exception {
		String specfile = args[0];
		specfile = "/home/crusherofheads/localgit/jgi_types/KBaseFile.spec";
//		specfile = "/home/crusherofheads/localgit/workspace_deluxe/workspace.spec";
//		specfile = "/home/crusherofheads/localgit/user_and_job_state/userandjobstate.spec";
		Reader specReader = new FileReader(specfile);
		new HTMLGenerator().generate(specReader,
				new FileIncludeProvider(
						new File("/home/crusherofheads/localgit/jgi_types")),
				new DiskFileSaver(new File("temp_html")));
	}
	
	private static class Res {
		final KbModule mod;
		final HTMLGenVisitor visitor;
		final Tag html;
		private Res(KbModule mod, HTMLGenVisitor visitor, Tag html) {
			this.mod = mod;
			this.visitor = visitor;
			this.html = html;
		}
	}
}
