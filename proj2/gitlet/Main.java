package gitlet;

import static gitlet.Utils.*;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Raiden Ei
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        if (args.length == 0) {
            message("Please enter a command.");
            return;
        }
        String firstArg = args[0];
        int len = args.length;
        switch (firstArg) {
            case "init":
                // TODO: handle the `init` command
                checkOperands(1, len);
                Repository.init();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                checkOperands(2, len);
                Repository.add(args[1]);
                break;
            // TODO: FILL THE REST IN
            case "commit":
                checkOperands(2, len);
                Repository.commit(args[1]);
                break;
            case "rm":
                checkOperands(2, len);
                Repository.rm(args[1]);
                break;
            case "log":
                checkOperands(1, len);
                Repository.log();
                break;
            case "global-log":
                checkOperands(1, len);
                Repository.globalLog();
                break;
            case "find":
                checkOperands(2, len);
                Repository.find(args[1]);
                break;
            case "status":
                checkOperands(1, len);
                Repository.status();
                break;
            case "checkout":
                Repository.checkout(args);
                break;
            case "branch":
                Repository.branch(args[1]);
                break;
            case "rm-branch":
                Repository.rmBranch(args[1]);
                break;
            case "reset":
                Repository.reset(args[1]);
                break;
            case "merge":
                Repository.merge(args[1]);
                break;
            default:message("No command with that name exists.");
        }
    }

    private static void checkOperands(int virtual, int actual) {
        if (virtual != actual) {
            message("Incorrect operands.");
            System.exit(0);
        }
    }
}
