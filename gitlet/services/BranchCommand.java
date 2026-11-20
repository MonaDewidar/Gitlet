package gitlet.services;

import gitlet.GitletException;
import gitlet.Utils;
import gitlet.repository.Branch;
import gitlet.repository.Path;

public class BranchCommand {
    public static void branchCommand(String branchName){
        if(Utils.join(Path.Branch_DIR,branchName).exists()){
            throw new GitletException("A branch with that name already exists.");
        }
       String commitId=Branch.readBranch(Branch.readHead());
        Branch.writeBranch(branchName,commitId);

    }

    public static void branchDeleting(String branchName){
        if(!(Utils.join(Path.Branch_DIR,branchName).isFile())){
            throw new GitletException("A branch with that name does not exist.");
        }
        String currentBranch=Branch.readHead();
        if(currentBranch.equals(branchName)){
            throw new GitletException("Cannot remove the current branch.");
        }
        Utils.join(Path.Branch_DIR,branchName).delete();

    }
}
