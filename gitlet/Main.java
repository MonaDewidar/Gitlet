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
                    if (args.length > 1) {
                        throw new GitletException("Incorrect operands.");
                    }
                    InitCommand.init();
                    break;
                case "add":
                    if(!InitCommand.isInitialized()){
                        throw new GitletException("Not in an initialized Gitlet directory.");
                    }
                    if(args.length!=2){
                        throw new GitletException("Incorrect operands.");
                    }
                    AddCommand.addingFunction(args[1]);
                    break;

                case "commit":
                    if(!InitCommand.isInitialized()){
                        throw new GitletException("Not in an initialized Gitlet directory.");
                    }
                    if(args.length!=2){
                        // message empty
                            throw new GitletException("Please enter a commit message.");
                    }
                    CommitCommand.addCommit(args[1]);
                    break;

                case "rm":
                    if(!InitCommand.isInitialized()){
                        throw new GitletException("Not in an initialized Gitlet directory.");
                    }
                    if(args.length!=2){
                        throw new GitletException("Incorrect operands.");
                    }
                    RmCommand.removeCommand(args[1]);
                    break;

                case "log":
                    if(args.length!=1){
                        throw new GitletException("Incorrect operands.");
                    }
                    LogCommand.logCommand();
                    break;

                case "global-log":
                    if(args.length!=1){
                        throw new GitletException("Incorrect operands.");
                    }
                    LogCommand.globalLogCommand();
                    break;

                case "find":
                    if(!InitCommand.isInitialized()){
                        throw new GitletException("Not in an initialized Gitlet directory.");
                    }
                    if(args.length!=2){
                        throw new GitletException("Incorrect operands.");
                    }
                    LogCommand.findCommand(args[1]);
                    break;

                case "status":
                    if(!InitCommand.isInitialized()){
                        throw new GitletException("Not in an initialized Gitlet directory.");
                    }
                    if(args.length!=1){
                        throw new GitletException("Incorrect operands.");
                    }
                    LogCommand.statusCommand();
                    break;

                case "checkout":
                    if(!InitCommand.isInitialized()){
                        throw new GitletException("Not in an initialized Gitlet directory.");
                    }
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
                    if(args.length!=2){
                        throw new GitletException("Incorrect operands.");
                    }
                    BranchCommand.branchCommand(args[1]);
                    break;

                case "rm-branch":
                    if(args.length!=2){
                        throw new GitletException("Incorrect operands.");
                    }
                    BranchCommand.branchDeleting(args[1]);
                    break;
                default:
                    throw new GitletException("No command with that name exists.");
            }
        }catch (GitletException e){
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }
}
