package gitlet.services;

import gitlet.GitletException;
import gitlet.repository.Branch;
import gitlet.repository.Commit;
import gitlet.repository.StagingArea;
import gitlet.repository.WorkingDirectory;

import java.util.Map;
import java.util.TreeMap;

/** The three forms of checkout. */
public class CheckoutCommand {

    static final String UNTRACKED_FILE_ERROR =
            "There is an untracked file in the way; delete it, or add and commit it first.";

    /** checkout -- [file name]: restores the file from the current commit. */
    public static void checkoutFile(String fileName) {
        restoreFileFromCommit(Branch.currentCommitId(), fileName);
    }

    /** checkout [commit id] -- [file name]: restores the file from a commit. The id can be abbreviated. */
    public static void checkoutFileFromCommit(String commitId, String fileName) {
        restoreFileFromCommit(Commit.findFullId(commitId), fileName);
    }

    /** checkout [branch name]: switches to the branch and restores its files. */
    public static void checkoutBranch(String branchName) {
        if (!Branch.exists(branchName)) {
            throw new GitletException("No such branch exists.");
        }
        if (branchName.equals(Branch.readHead())) {
            throw new GitletException("No need to checkout the current branch.");
        }
        checkoutCommit(Branch.readBranch(branchName));
        Branch.writeHead(branchName);
    }

    /** Writes the file's version in this commit to the working directory. The staging area does not change. */
    private static void restoreFileFromCommit(String commitId, String fileName) {
        String blobId = Commit.loadCommit(commitId).getBlobs().get(fileName);
        if (blobId == null) {
            throw new GitletException("File does not exist in that commit.");
        }
        WorkingDirectory.restoreFile(fileName, blobId);
    }

    /**
     * Makes the working directory match this commit and clears the staging area.
     * Used by checkout [branch name] and by reset.
     */
    static void checkoutCommit(String targetCommitId) {
        TreeMap<String, String> currentFiles = Commit.loadCommit(Branch.currentCommitId()).getBlobs();
        TreeMap<String, String> targetFiles = Commit.loadCommit(targetCommitId).getBlobs();

        // Check before changing anything: an untracked file must not be overwritten.
        for (String fileName : WorkingDirectory.listFiles()) {
            if (!currentFiles.containsKey(fileName) && targetFiles.containsKey(fileName)) {
                throw new GitletException(UNTRACKED_FILE_ERROR);
            }
        }

        for (Map.Entry<String, String> entry : targetFiles.entrySet()) {
            WorkingDirectory.restoreFile(entry.getKey(), entry.getValue());
        }
        for (String fileName : currentFiles.keySet()) {
            if (!targetFiles.containsKey(fileName)) {
                WorkingDirectory.deleteFile(fileName);
            }
        }

        StagingArea stagingArea = StagingArea.load();
        stagingArea.clear();
        stagingArea.save();
    }
}
