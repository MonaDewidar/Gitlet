package gitlet.services;

import gitlet.GitletException;
import gitlet.repository.Branch;

/** branch and rm-branch. */
public class BranchCommand {

    /** branch [branch name]: creates a branch at the current commit. Does not switch to it. */
    public static void createBranch(String branchName) {
        if (Branch.exists(branchName)) {
            throw new GitletException("A branch with that name already exists.");
        }
        Branch.writeBranch(branchName, Branch.currentCommitId());
    }

    /** rm-branch [branch name]: deletes the branch pointer only. Its commits are kept. */
    public static void removeBranch(String branchName) {
        if (!Branch.exists(branchName)) {
            throw new GitletException("A branch with that name does not exist.");
        }
        if (branchName.equals(Branch.readHead())) {
            throw new GitletException("Cannot remove the current branch.");
        }
        Branch.delete(branchName);
    }
}
