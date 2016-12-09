package us.kbase.tools;

import java.io.IOException;

public class WinShortPath {
    public static void main(String[] args) {
        if (args.length != 1) {
            String usage = "java " + WinShortPath.class.getName() + " longfilename";
            System.err.println(usage);
            System.exit(1);
        } else {
            try {
                System.out.println(getWinShortPath(args[0]));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String getWinShortPath(String path) throws IOException, InterruptedException {

            Process process =
                Runtime.getRuntime().exec(
                        "cmd /c for %I in (\"" + path + "\") do @echo %~fsI");
            process.waitFor();

            byte[] data = new byte[65536];
            int size = process.getInputStream().read(data);

            if (size <= 0)
                return null;

            return new String(data, 0, size).replaceAll("\\r\\n", "");
    }
}
