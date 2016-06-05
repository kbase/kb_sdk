package us.kbase.mobu.compiler.html;

import static j2html.TagCreator.body;
import static j2html.TagCreator.document;
import static j2html.TagCreator.head;
import static j2html.TagCreator.html;
import static j2html.TagCreator.link;
import static j2html.TagCreator.title;

import j2html.tags.Tag;

import java.io.BufferedReader;
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
import us.kbase.jkidl.ParseException;
import us.kbase.jkidl.SpecParser;
import us.kbase.kidl.KbModule;
import us.kbase.kidl.KidlParseException;
import us.kbase.mobu.util.DiskFileSaver;
import us.kbase.mobu.util.FileSaver;

public class HTMLGenerator {

	public HTMLGenerator() {}
	
	//TODO HTML TEST test with spec A imports B which imports C but A doesn't use types from B that link to C
	//TODO HTML TEST test with spec with <script> tags to ensure they don't execute
	//TODO HTML javadoc
	//TODO HTML WARN how handle warnings? Logger makes most sense
	//TODO HTML include CSS file
	//TODO HTML LOWPRIO figure out what's going on with comment whitespace & fix
	//TODO HTML jars: j2HTML licence & push
	//TODO HTML TEST test with and without default auth
	public void generate(
			final Reader spec,
			final IncludeProvider includes,
			final FileSaver saver)
			throws KidlParseException, IOException {
		final MemoizingIncludeProvider memo =
				new MemoizingIncludeProvider(includes);
		final SpecParser p = new SpecParser(new BufferedReader(spec));
		final Map<String, KbModule> root;
		try {
			root = p.SpecStatement(memo);
			spec.close();
		} catch (ParseException e) {
			throw new KidlParseException(e.getMessage(), e);
		}
		if (root.size() > 1) {
			throw new IllegalStateException("A programming error occured. " +
					"There should only be one entry in the parsed structure " +
					"map");
		}
		KbModule main = null;
		for (final KbModule m: root.values()) {
			main = m;
		}
		final Map<String, Res> modules = new HashMap<String, Res>();
		final HTMLGenVisitor visitor =
				new HTMLGenVisitor(main.getModuleName());
		modules.put(main.getModuleName(), new Res(main, visitor,
				main.accept(visitor)));
		for (final KbModule m: memo.getParsed().values()) {
			final HTMLGenVisitor v =
					new HTMLGenVisitor(m.getModuleName());
			modules.put(m.getModuleName(), new Res(m, v, m.accept(v)));
		}
		//TODO HTML WARN for bad links in dep types and methods
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
				body().with(res.visitor.renderIncludes()).with(res.html)
				);
		
		//TODO HTML typedef & funcdef indexes
		try (final Writer w = saver.openWriter(
				res.mod.getModuleName() + HTMLGenVisitor.DOT_HTML)) {
			w.write(document().render());
			w.write(page.render());
		}
	}
	
	public static void main(String[] args) throws Exception {
		String specfile = args[0];
		specfile = "/home/crusherofheads/localgit/jgi_types/KBaseFile.spec";
//		specfile = "/home/crusherofheads/localgit/workspace_deluxe/workspace.spec";
//		specfile = "/home/crusherofheads/localgit/user_and_job_state/userandjobstate.spec";
		new HTMLGenerator().generate(new FileReader(specfile),
				new FileIncludeProvider(
//						new File(".")),
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
