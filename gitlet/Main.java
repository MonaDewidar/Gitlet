package gitlet;
import gitlet.services.*;

public class Main {

    public static void main(String[] args) {

        try {
            if(args.length==0){
                throw new GitletException("Please enter a command.");
            }
            String command=args[0];
            switch (command) {
                case "init":
                    validateNumArgs(args,1);
                    InitCommand.init();
                    break;
                case "add":
                    checkInitialized();
                    validateNumArgs(args,2);
                    AddCommand.addingFunction(args[1]);
                    break;

                case "commit":
                    checkInitialized();
                    if(args.length==1){
                        // message empty
                        throw new GitletException("Please enter a commit message.");
                    }
                    validateNumArgs(args,2);
                    CommitCommand.addCommit(args[1]);
                    break;

                case "rm":
                    checkInitialized();
                    validateNumArgs(args,2);
                    RmCommand.removeCommand(args[1]);
                    break;

                case "log":
                    checkInitialized();
                    validateNumArgs(args,1);
                    LogCommand.logCommand();
                    break;

                case "global-log":
                    checkInitialized();
                    validateNumArgs(args,1);
                    LogCommand.globalLogCommand();
                    break;

                case "find":
                    checkInitialized();
                    validateNumArgs(args,2);
                    LogCommand.findCommand(args[1]);
                    break;

                case "status":
                    checkInitialized();
                    validateNumArgs(args,1);
                    LogCommand.statusCommand();
                    break;

                case "checkout":
                    checkInitialized();
                    if(args.length==3 && args[1].equals("--")){
                        CheckoutCommand.checkoutFile(args[2]);
                    }
                    else if(args.length==4 && args[2].equals("--")){
                        CheckoutCommand.checkoutFileWithSpecificCommitId(args[1],args[3]);
                    }
                    else if(args.length==2 ){
                        CheckoutCommand.checkoutBranch(args[1]);
                    }
                    else {
                        throw new GitletException("Incorrect operands.");
                    }
                    break;

                case "branch":
                    checkInitialized();
                    validateNumArgs(args,2);
                    BranchCommand.branchCommand(args[1]);
                    break;

                case "rm-branch":
                    checkInitialized();
                    validateNumArgs(args,2);
                    BranchCommand.branchDeleting(args[1]);
                    break;

                case "reset":
                    checkInitialized();
                    validateNumArgs(args,2);
                    ResetCommand.restCommand(args[1]);
                    break;

                case "merge":
                    checkInitialized();
                    validateNumArgs(args,2);
                    MergeCommand.mergeCommand(args[1]);
                    break;
                default:
                    throw new GitletException("No command with that name exists.");
            }
        }catch (GitletException e){
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }

    // every command except init needs the .gitlet directory
    private static void checkInitialized(){
        if(!InitCommand.isInitialized()){
            throw new GitletException("Not in an initialized Gitlet directory.");
        }
    }

    private static void validateNumArgs(String[] args, int n){
        if(args.length!=n){
            throw new GitletException("Incorrect operands.");
        }
    }
}
