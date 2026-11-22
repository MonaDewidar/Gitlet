package gitlet;

import gitlet.services.AddCommand;
import gitlet.services.BranchCommand;
import gitlet.services.CheckoutCommand;
import gitlet.services.CommitCommand;
import gitlet.services.InitCommand;
import gitlet.services.LogCommand;
import gitlet.services.MergeCommand;
import gitlet.services.ResetCommand;
import gitlet.services.RmCommand;
import gitlet.services.StatusCommand;

/**
 * Entry point of Gitlet: java gitlet.Main [command] [operands].
 * Checks the operands and calls the command. Every error is a GitletException,
 * whose message is printed before exiting.
 */
public class Main {

    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                throw new GitletException("Please enter a command.");
            }
            runCommand(args);
        } catch (GitletException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }

    private static void runCommand(String[] args) {
        String command = args[0];
        switch (command) {
            case "init":
                validateNumArgs(args, 1);
                InitCommand.init();
                break;
            case "add":
                checkInitialized();
                validateNumArgs(args, 2);
                AddCommand.add(args[1]);
                break;
            case "commit":
                checkInitialized();
                if (args.length == 1) {
                    throw new GitletException("Please enter a commit message.");
                }
                validateNumArgs(args, 2);
                CommitCommand.commit(args[1]);
                break;
            case "rm":
                checkInitialized();
                validateNumArgs(args, 2);
                RmCommand.remove(args[1]);
                break;
            case "log":
                checkInitialized();
                validateNumArgs(args, 1);
                LogCommand.log();
                break;
            case "global-log":
                checkInitialized();
                validateNumArgs(args, 1);
                LogCommand.globalLog();
                break;
            case "find":
                checkInitialized();
                validateNumArgs(args, 2);
                LogCommand.find(args[1]);
                break;
            case "status":
                checkInitialized();
                validateNumArgs(args, 1);
                StatusCommand.status();
                break;
            case "checkout":
                checkInitialized();
                checkout(args);
                break;
            case "branch":
                checkInitialized();
                validateNumArgs(args, 2);
                BranchCommand.createBranch(args[1]);
                break;
            case "rm-branch":
                checkInitialized();
                validateNumArgs(args, 2);
                BranchCommand.removeBranch(args[1]);
                break;
            case "reset":
                checkInitialized();
                validateNumArgs(args, 2);
                ResetCommand.reset(args[1]);
                break;
            case "merge":
                checkInitialized();
                validateNumArgs(args, 2);
                MergeCommand.merge(args[1]);
                break;
            default:
                throw new GitletException("No command with that name exists.");
        }
    }

    /** Chooses one of the three forms of checkout from its operands. */
    private static void checkout(String[] args) {
        if (args.length == 3 && args[1].equals("--")) {
            CheckoutCommand.checkoutFile(args[2]);
        } else if (args.length == 4 && args[2].equals("--")) {
            CheckoutCommand.checkoutFileFromCommit(args[1], args[3]);
        } else if (args.length == 2) {
            CheckoutCommand.checkoutBranch(args[1]);
        } else {
            throw new GitletException("Incorrect operands.");
        }
    }

    /** Every command except init needs a .gitlet directory. */
    private static void checkInitialized() {
        if (!InitCommand.isInitialized()) {
            throw new GitletException("Not in an initialized Gitlet directory.");
        }
    }

    private static void validateNumArgs(String[] args, int expected) {
        if (args.length != expected) {
            throw new GitletException("Incorrect operands.");
        }
    }
}
