package gitlet.services;

import gitlet.repository.*;

public class AddCommand {
    public static void addingFunction(String fileName ){
        // get file content
        byte[] content= WorkingDirectory.readFilesInWD(fileName);
        // get id_blob
        String blobId=Blob.idOf(content);
        // good point
        StagingArea stagingArea=StagingArea.loadStagingContent();

        // read the current commit
        String branchName= Branch.readHead();
        String commitId=Branch.readBranch(branchName);
        Commit current=Commit.loadCommit(commitId);
        String currentCommitBlobId=current.getBlobs().get(fileName);
        if(blobId.equals(currentCommitBlobId)){
            // no change -> not staged for addition and not staged for removal anymore
            stagingArea.unstage(fileName);
        }
        else {
            Blob.saveContent(content);
            stagingArea.addingFiles(fileName,blobId);
        }

        stagingArea.saveStagingContent();

    }
}
