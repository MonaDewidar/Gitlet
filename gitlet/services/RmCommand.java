package gitlet.services;

import gitlet.GitletException;
import gitlet.repository.Branch;
import gitlet.repository.Commit;
import gitlet.repository.StagingArea;
import gitlet.repository.WorkingDirectory;

/** rm [file name]: unstages a file, or stages it for removal if the current commit tracks it. */
public class RmCommand {

    public static void remove(String fileName) {
        StagingArea stagingArea = StagingArea.load();
        Commit currentCommit = Commit.loadCommit(Branch.currentCommitId());

        boolean staged = stagingArea.getStagedFiles().containsKey(fileName);
        boolean tracked = currentCommit.getBlobs().containsKey(fileName);
        if (!staged && !tracked) {
            throw new GitletException("No reason to remove the file.");
        }

        if (staged) {
            stagingArea.unstage(fileName);
        }
        if (tracked) {
            // The next commit will not track it. The current commit does not change.
            stagingArea.stageForRemoval(fileName);
            WorkingDirectory.deleteFile(fileName);
        }
        stagingArea.save();
    }
}
