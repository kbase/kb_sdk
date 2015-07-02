package us.kbase.jkidl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class Utils {
	public static String trim(String comment) {
		if (comment == null)
			return "";
		StringBuilder sb = new StringBuilder(comment.trim());
		if (sb.substring(0, 2).equals("/*"))
			sb.delete(0, 2);
		if (sb.substring(sb.length() - 2, sb.length()).equals("*/"))
			sb.delete(sb.length() - 2, sb.length());
		StringBuilder sb2 = new StringBuilder();
		for (int pos = 0; pos < sb.length(); pos++)
			if (sb.charAt(pos) == '\t') {
				sb2.append("        ");
			} else {
				sb2.append(sb.charAt(pos));
			}
		sb = sb2;
		int base = 0;
        BufferedReader br = new BufferedReader(new StringReader(sb.toString()));
        sb = new StringBuilder();
        try {
            for (int lineNum = 0;; lineNum++) {
                String l = br.readLine();
                if (l == null)
                    break;
                if (lineNum == 1)
                    while (base < l.length() && l.charAt(base) == ' ')
                        base++;
                if (l.length() >= base && l.substring(0, base).trim().length() == 0)
                    l = l.substring(base);
                sb.append(l).append('\n');
            }
            br.close();
        } catch (IOException ex) {
            throw new IllegalStateException("Unexpected error", ex);
        }
		trimWhitespaces(sb);
		return sb.toString();
	}

    public static void trimWhitespaces(StringBuilder sb) {
        while (sb.length() > 0) {
			char ch = sb.charAt(0);
			if (ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n') {
				sb.delete(0, 1);
			} else {
				break;
			}
		}
		while (sb.length() > 0) {
			char ch = sb.charAt(sb.length() - 1);
			if (ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n') {
				sb.delete(sb.length() - 1, sb.length());
			} else {
				break;
			}
		}
    }
}
