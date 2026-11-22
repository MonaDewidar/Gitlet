package gitlet.services;

import gitlet.repository.Blob;
import gitlet.repository.Branch;
import gitlet.repository.Commit;
import gitlet.repository.StagingArea;
import gitlet.repository.WorkingDirectory;

/** add [file name]: stages the current version of a file for the next commit. */
public class AddCommand {

    public static void add(String fileName) {
        byte[] content = WorkingDirectory.readFile(fileName);
        String blobId = Blob.idOf(content);

        StagingArea stagingArea = StagingArea.load();
        Commit currentCommit = Commit.loadCommit(Branch.currentCommitId());

        if (blobId.equals(currentCommit.getBlobs().get(fileName))) {
            // Same as in the current commit: nothing to stage, and it is not staged for removal anymore.
            stagingArea.unstage(fileName);
        } else {
            Blob.save(content);
            stagingArea.stageForAddition(fileName, blobId);
        }
        stagingArea.save();
    }
}
