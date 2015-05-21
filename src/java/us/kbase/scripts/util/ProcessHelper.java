package us.kbase.scripts.util;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: Roman
 * Date: 08.01.12
 */
public class ProcessHelper {
    private StringWriter outWr = new StringWriter();
    private PrintWriter outPw = new PrintWriter(outWr);
    private StringWriter errWr = new StringWriter();
    private PrintWriter errPw = new PrintWriter(errWr);
    private Process process;
    private int exitCode = -1;
    //
    public static enum OutType {
        SystemOut, SystemErr, StringOut, StringErr, Null
    }

    public static ProcessHelper exec(String cmd, File workDir, boolean waitFor) throws IOException {
        return exec(new CommandHolder(cmd), workDir, waitFor);
    }

    public static ProcessHelper exec(CommandHolder cmd, File workDir, boolean waitFor) throws IOException {
        return new ProcessHelper(cmd, workDir, waitFor);
    }

    public static ProcessHelper exec(String cmd, File workDir, File input, File output, boolean waitFor) throws IOException {
        return exec(new CommandHolder(cmd), workDir, input, output, waitFor);
    }

    public static ProcessHelper exec(CommandHolder cmd, File workDir, File input, File output, boolean waitFor) throws IOException {
        return exec(cmd, workDir, input, output, null, waitFor);
    }

    public static ProcessHelper exec(CommandHolder cmd, File workDir, File input, File output, File error, boolean waitFor) throws IOException {
        BufferedReader br = input == null ? null : new BufferedReader(new FileReader(input));
        PrintWriter pw = output == null ? null : new PrintWriter(output);
        PrintWriter epw = error == null ? null : new PrintWriter(error);
        ProcessHelper ret = exec(cmd, workDir, br, pw, epw, waitFor);
        if (br != null)
            br.close();
        if (pw != null)
            pw.close();
        if (epw != null)
            epw.close();
        return ret;
    }

    public static ProcessHelper exec(String cmd, File workDir, BufferedReader input, PrintWriter output,
                                     PrintWriter error, boolean waitFor) throws IOException {
        return exec(new CommandHolder(cmd), workDir, input, output, error, waitFor);
    }

    public static ProcessHelper exec(CommandHolder cmd, File workDir, BufferedReader input, PrintWriter output,
                                     PrintWriter error, boolean waitFor) throws IOException {
        return new ProcessHelper(cmd, workDir, OutType.StringOut, OutType.StringErr, input, output, error, waitFor);
    }

    public static ProcessHelper exec(CommandHolder cmd, File workDir, BufferedReader input, boolean saveOutput, boolean saveErrors, boolean waitFor) throws IOException {
        return new ProcessHelper(cmd, workDir, saveOutput ? OutType.StringOut : OutType.SystemOut, 
                saveErrors ? OutType.StringErr : OutType.SystemErr, input, null, null, waitFor);
    }

    public ProcessHelper(CommandHolder cmd, File workDir, boolean waitFor) throws IOException {
        this(cmd, workDir, OutType.SystemOut, OutType.SystemErr, null, null, null, waitFor);
    }

    public ProcessHelper(CommandHolder cmd, File workDir, OutType outType, OutType errType,
                         BufferedReader input, PrintWriter output, PrintWriter error, boolean waitFor) throws IOException {
        if (output != null)
            outPw = output;
        if (error != null)
            errPw = error;
        process = cmd.cmdLine != null ? Runtime.getRuntime().exec(cmd.cmdLine, null, workDir) :
                Runtime.getRuntime().exec(cmd.cmdParts, null, workDir);
        Thread outTh = readInNewThread(process.getInputStream(), outType);
        Thread errTh = readInNewThread(process.getErrorStream(), errType);
        if (input != null) {
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(process.getOutputStream()));
            while (true) {
                String line = input.readLine();
                if (line == null)
                    break;
                pw.println(line);
            }
            pw.close();
        }
        try {
            outTh.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            errTh.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (waitFor)
            try {
                exitCode = process.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    public Process getProcess() {
		return process;
	}
    
    private Thread readInNewThread(final InputStream is, final OutType outType) {
        Thread ret = new Thread(new Runnable() {
            public void run() {
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    while (true) {
                        String line = br.readLine();
                        if (line == null)
                            break;
                        switch (outType) {
                            case SystemOut:
                                System.out.println(line); break;
                            case SystemErr:
                                System.err.println(line); break;
                            case Null:
                                break;
                            case StringOut:
                                outPw.println(line); break;
                            case StringErr:
                                errPw.println(line); break;
                        }
                    }
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new IllegalStateException("Error reading data from executed process", e);
                }
            }
        });
        ret.start();
        return ret;
    }

    public int getExitCode() {
        return exitCode;
    }

    public String getSavedOutput() {
        return outWr.toString();
    }

    public String getSavedErrors() {
        return errWr.toString();
    }

    public static CommandHolder cmd(String... cmdParts) {
        return new CommandHolder(true, cmdParts);
    }

    public static class CommandHolder {
        public String cmdLine;
        public String[] cmdParts;
        public boolean waitFor = true;

        public CommandHolder(String cmdLine) {
            this.cmdLine = cmdLine;
        }

        public CommandHolder(boolean asParts, String... cmdParts) {
            if (asParts) {
                this.cmdParts = cmdParts;
            } else {
                if (cmdParts.length != 1)
                    throw new IllegalStateException("Error extracting command line, line count: " + cmdParts.length);
                cmdLine = cmdParts[0];
            }
        }

        public CommandHolder add(String... newCmdParts) {
            if (cmdLine != null)
                throw new IllegalStateException("Command was defined as one line");
            List<String> list = new ArrayList<String>(Arrays.asList(cmdParts));
            list.addAll(Arrays.asList(newCmdParts));
            cmdParts = list.toArray(new String[list.size()]);
            return this;
        }

        public CommandHolder dontWaitFor() {
        	waitFor = false;
        	return this;
        }
        
        public ProcessHelper exec(File workDir) throws IOException {
            return ProcessHelper.exec(this, workDir, waitFor);
        }

        public ProcessHelper exec(File workDir, File input, File output) throws IOException {
            return ProcessHelper.exec(this, workDir, input, output, waitFor);
        }

        public ProcessHelper exec(File workDir, File input, File output, File error) throws IOException {
            return ProcessHelper.exec(this, workDir, input, output, error, waitFor);
        }

        public ProcessHelper exec(File workDir, BufferedReader input, PrintWriter output) throws IOException {
            return ProcessHelper.exec(this, workDir, input, output, null, waitFor);
        }

        public ProcessHelper exec(File workDir, BufferedReader input, PrintWriter output, PrintWriter error) throws IOException {
            return ProcessHelper.exec(this, workDir, input, output, error, waitFor);
        }

        public ProcessHelper exec(File workDir, BufferedReader input, boolean saveOutput, boolean saveErrors) throws IOException {
            return ProcessHelper.exec(this, workDir, input, saveOutput, saveErrors, waitFor);
        }
    }
}
