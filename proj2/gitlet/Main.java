package gitlet;

import java.io.File;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Joanne Lee
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }


        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                Repository.init();
                break;
            case "add":
                validateArgs("add", args, 2);
                String addFile = args[1];
                Repository.add(addFile);
                break;
            case "commit":
                validateArgs("commit", args, 2);
                String message = args[1];
                Repository.commit(message);
                break;
            case "rm":
                validateArgs("rm", args, 2);
                String rmFile = args[1];
                Repository.rm(rmFile);
                break;
            case "log":
                validateArgs("log", args, 1);
                Repository.log();
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }


    public static void validateArgs(String cmd, String[] args, int n) {
        if (args.length != n) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }
}
