package gitlet;


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
                validateNumArgs(args, 2);
                String addFile = args[1];
                Repository.add(addFile);
                break;
            case "commit":
                validateNumArgs(args, 2);
                String message = args[1];
                Repository.commit(message);
                break;
            case "rm":
                validateNumArgs(args, 2);
                String rmFile = args[1];
                Repository.rm(rmFile);
                break;
            case "log":
                validateNumArgs(args, 1);
                Repository.log();
                break;
            case "global-log":
                validateNumArgs(args, 1);
                Repository.globalLog();
                break;
            case "find":
                validateNumArgs(args, 2);
                String commitMessage = args[1];
                Repository.find(commitMessage);
                break;
            case "status":
                validateNumArgs(args, 1);
                Repository.status();
                break;
            case "checkout":
                switch(args.length) {
                    case 2:
                        Repository.checkoutBranch(args[1]);
                        break;
                    case 3:
                        Repository.checkoutFile(args[2]);
                        break;
                    case 4:
                        Repository.checkoutFile(args[1], args[3]);
                        break;
                    default:
                }
                break;
            case "branch":
                validateNumArgs(args, 2);
                String branchName = args[1];
                Repository.branch(branchName);
                break;
            case "rm-branch":
                validateNumArgs(args, 2);
                String rmBranchName = args[1];
                Repository.rmBranch(rmBranchName);
                break;
            case "reset":
                validateNumArgs(args, 2);
                String commitID = args[1];
                Repository.reset(commitID);
                break;

            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }


    public static void validateNumArgs(String[] args, int n) {
        if (args.length != n) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }
}
