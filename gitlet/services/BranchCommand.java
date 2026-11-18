package gitlet.services;

import gitlet.GitletException;
import gitlet.Utils;
import gitlet.repository.Branch;
import gitlet.repository.Path;

public class BranchCommand {
    public static void branchCommand(String branchName){
        if(Utils.plainFilenamesIn(Path.Branch_DIR).contains(branchName)){
            throw new GitletException("A branch with that name already exists.");
        }
       String commitId=Branch.readBranch(Branch.readHead());
        Branch.writeBranch(branchName,commitId);

    }
}
