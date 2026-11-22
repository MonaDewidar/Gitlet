package gitlet.services;

import gitlet.repository.Branch;
import gitlet.repository.Commit;

/** reset [commit id]: moves the current branch to a commit and restores that commit's files. */
public class ResetCommand {

    public static void reset(String commitId) {
        // The id can be abbreviated.
        String fullId = Commit.findFullId(commitId);

        // Restore the commit's files, delete the tracked files that are not in it, clear the staging area.
        CheckoutCommand.checkoutCommit(fullId);

        Branch.writeBranch(Branch.readHead(), fullId);
    }
}
