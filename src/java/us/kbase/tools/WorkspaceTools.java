package us.kbase.tools;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

public class WorkspaceTools {
    private static final String WORKSPACE_TOOLS_SH_NAME = "ws-tools";

    private static final String FASTX2READS_COMMAND     = "fastX2reads";
    private static final String HELP_COMMAND            = "help";

    public static void main(String[] args) {
        GlobalArgs gArgs = new GlobalArgs();
        JCommander jc = new JCommander(gArgs);
        jc.setProgramName("ws-tools");

        HelpCommandArgs help = new HelpCommandArgs();
        jc.addCommand(HELP_COMMAND, help);

        FastX2reads.CommandArgs fastX2readsArgs = new FastX2reads.CommandArgs();
        jc.addCommand(FASTX2READS_COMMAND, fastX2readsArgs);

        try {
            jc.parse(args);
        } catch (RuntimeException e) {
            showError("Command Line Argument Error", e.getMessage());
            System.exit(1);
        }
        
        // if the help flag is set, then show some help and exit
        if(gArgs.help) {
            showBriefHelp(jc, System.out);
            return;
        }
        
        // no command entered, just print brief info and exit
        if(jc.getParsedCommand()==null) {
            showBriefHelp(jc, System.out);
            return;
        }
        
        // if we get here, we have a valid command, so process it and do stuff
        int returnCode = 0;
        if(jc.getParsedCommand().equals(HELP_COMMAND)) {
            showCommandUsage(jc, help, System.out);
        } else if(jc.getParsedCommand().equals(FASTX2READS_COMMAND)) {
            returnCode = runFastX2readsCommand(fastX2readsArgs,jc);
        } 
        
        if(returnCode!=0) {
            System.exit(returnCode);
        }
    }
    
    private static int runFastX2readsCommand(FastX2reads.CommandArgs cmdArgs, JCommander jc) {
        // Figure out module name.
        // Join together spaced out names with underscores if necessary.
        try {
            FastX2reads tester = new FastX2reads();
            tester.transform(cmdArgs);
        }
        catch (Exception e) {
            showError("Error while transforming fastX file(s) into Reads object", e.getMessage());
            return 1;
        }
        return 0;
    }

    private static void showBriefHelp(JCommander jc, PrintStream out) {
        Map<String,JCommander> commands = jc.getCommands();
        out.println("");
        out.println(WORKSPACE_TOOLS_SH_NAME + " - KBase Workspace Transform Tools for basic data/file types");
        out.println("");
        out.println("usage: " + WORKSPACE_TOOLS_SH_NAME + " <command> [options]");
        out.println("");
        out.println("    The available commands are:");
        for (Map.Entry<String, JCommander> command : commands.entrySet()) {
            out.println("        " + command.getKey() +" - "+jc.getCommandDescription(command.getKey()));
        }
        out.println("");
        out.println("    For help on a specific command, see \"" + WORKSPACE_TOOLS_SH_NAME + " help <command>\".");
        out.println("    For full usage information, see \"" + WORKSPACE_TOOLS_SH_NAME + " help -a\".");
        out.println("");
    };
    
    private static void showCommandUsage(JCommander jc, HelpCommandArgs helpArgs, PrintStream out) {
        if (helpArgs.showAll) {
            showFullUsage(jc, out);
        } else if (helpArgs.command != null && helpArgs.command.size() > 0) {
            String indent = "";
            StringBuilder usage = new StringBuilder();
            for(String cmd : helpArgs.command) {
                if(jc.getCommands().containsKey(cmd)) {
                    usage.append(WORKSPACE_TOOLS_SH_NAME + " " + cmd + "\n");
                    jc.usage(cmd, usage, indent + "    ");
                    usage.append("\n");
                } else {
                    out.println("Command \"" + cmd + "\" is not a valid command.  To view available commands, run:");
                    out.println("    " + WORKSPACE_TOOLS_SH_NAME + " " + HELP_COMMAND);
                    return;
                }
            }
            out.print(usage.toString());
        } else {
            showBriefHelp(jc, out);
        }
    };
    
    private static void showFullUsage(JCommander jc, PrintStream out) {
        String indent = "";
        StringBuilder usage = new StringBuilder();
        jc.usage(usage, indent);
        out.print(usage.toString());
    };
    
    
    private static void showError(String error, String message, String extraHelp) {
        System.err.println(error + ": " + message+"\n");
        if(!extraHelp.isEmpty())
            System.err.println(extraHelp+"\n");
        System.err.println("For more help and usage information, run:");
        System.err.println("    " + WORKSPACE_TOOLS_SH_NAME + " " + HELP_COMMAND);
        System.err.println("");
    }
    
    private static void showError(String error, String message) {
        showError(error,message,"");
    }
    
    public static class GlobalArgs {
        @Parameter(names = {"-h","--help"}, help = true, description="Display help and full usage information.")
        boolean help;
    }

    @Parameters(commandDescription = "Get help and usage information.")
    private static class HelpCommandArgs {
        @Parameter(description="Show usage for this command")
        List<String> command;
        @Parameter(names={"-a","--all"}, description="Show usage for all commands")
        boolean showAll = false;
    }
}
