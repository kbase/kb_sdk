package us.kbase.scripts;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.Writer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class TextUtils {
	public static String capitalize(String text) {
		return capitalize(text, false);
	}
	
	public static String inCamelCase(String text) {
		return capitalize(text, true);
	}
		
	public static String capitalize(String text, boolean camel) {
		StringBuilder ret = new StringBuilder();
		StringTokenizer st = new StringTokenizer(text, "_-");
		boolean firstToken = true;
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (Character.isLowerCase(token.charAt(0)) && !(camel && firstToken)) {
				token = token.substring(0, 1).toUpperCase() + token.substring(1);
			}
			if (camel && firstToken && Character.isUpperCase(token.charAt(0))) {
				token = token.substring(0, 1).toLowerCase() + token.substring(1);				
			}
			ret.append(token);
			firstToken = false;
		}
		return ret.toString();
	}
	
	public static List<String> readFileLines(File f) throws IOException {
		return readStreamLines(new FileInputStream(f));
	}

	public static String readFileText(File f) throws IOException {
		return readStreamText(new FileInputStream(f));
	}

	public static List<String> readStreamLines(InputStream is) throws IOException {
		return readStreamLines(is, true);
	}
	
	public static List<String> readStreamLines(InputStream is, boolean closeAfter) throws IOException {
		List<String> ret = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		while (true) {
			String l = br.readLine();
			if (l == null)
				break;
			ret.add(l);
		}
		if (closeAfter)
			br.close();
		return ret;
	}
	
	public static void writeFileLines(List<String> lines, File targetFile) throws IOException {
	    writeFileLines(lines, new FileWriter(targetFile));
	}

	public static void writeFileLines(List<String> lines, Writer targetFile) throws IOException {
		PrintWriter pw = new PrintWriter(targetFile);
		for (String l : lines)
			pw.println(l);
		pw.close();
	}
	
	public static void copyStreams(InputStream is, OutputStream os) throws IOException {
		byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) > 0) {
            os.write(buffer, 0, length);
        }
        is.close();
        os.close();
	}

	public static String readStreamText(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		copyStreams(is, baos);
		return new String(baos.toByteArray());
	}

	public static void deleteRecursively(File fileOrDir) {
		if (fileOrDir.isDirectory() && !Files.isSymbolicLink(fileOrDir.toPath()))
			for (File f : fileOrDir.listFiles()) 
				deleteRecursively(f);
		fileOrDir.delete();
	}

    public static List<String> getLines(String text) throws Exception {
        BufferedReader br = new BufferedReader(new StringReader(text));
        List<String> ret = new ArrayList<String>();
        while (true) {
            String l = br.readLine();
            if (l == null)
                break;
            ret.add(l);
        }
        br.close();
        return ret;
    }
}
