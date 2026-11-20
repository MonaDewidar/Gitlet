package gitlet.services;

import gitlet.repository.Branch;
import gitlet.repository.Commit;

public class ResetCommand {
    public static void restCommand(String commitIdAp){
        // works with the full id or an abbreviated one
        String commitId=Commit.findFullId(commitIdAp);

        // checkout all files of this commit, remove the tracked files that are not in it,
        // and clear the staging area
        CheckoutCommand.checkoutCommit(commitId);

        // move the current branch head to this commit
        Branch.writeBranch(Branch.readHead(),commitId);
    }
}
