package gitlet.services;

import gitlet.GitletException;
import gitlet.repository.Branch;
import gitlet.repository.Commit;
import gitlet.repository.StagingArea;

import java.util.Date;
import java.util.TreeMap;

/** commit [message]: saves a snapshot of the tracked files and the staged changes. */
public class CommitCommand {

    public static void commit(String message) {
        commit(message, null);
    }

    /**
     * Makes a new commit on the current branch.
     * secondParent is the given branch's head for a merge commit, and null otherwise.
     */
    public static void commit(String message, String secondParent) {
        if (message == null || message.trim().isEmpty()) {
            throw new GitletException("Please enter a commit message.");
        }
        StagingArea stagingArea = StagingArea.load();
        if (stagingArea.isEmpty()) {
            throw new GitletException("No changes added to the commit.");
        }

        String branchName = Branch.readHead();
        String parentId = Branch.readBranch(branchName);
        Commit parent = Commit.loadCommit(parentId);

        // Start from the parent's files, then apply the staged additions and removals.
        TreeMap<String, String> blobs = new TreeMap<>(parent.getBlobs());
        blobs.putAll(stagingArea.getStagedFiles());
        for (String fileName : stagingArea.getRemovedFiles()) {
            blobs.remove(fileName);
        }

        Commit newCommit = new Commit(message, new Date(), parentId, secondParent, blobs);
        String id = newCommit.saveCommit();
        Branch.writeBranch(branchName, id);

        stagingArea.clear();
        stagingArea.save();
    }
}
