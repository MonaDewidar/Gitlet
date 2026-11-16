package gitlet.services;

import gitlet.repository.*;

import java.util.List;

public class AddCommand {
    public static void addingFunction(String fileName ){
        // get file content
        String content= WorkingDirectory.readFilesInWD(fileName);
        // get id_blob
        String blobId=Blob.saveContent(content);
        // good point
        StagingArea stagingArea=StagingArea.loadStagingContent();

        // read the current commit
        String branchName= Branch.readHead();
        String commitId=Branch.readBranch(branchName);
        Commit current=Commit.loadCommit(commitId);
        String currentCommitBlobId=current.getBlobs().get(fileName);
        if(blobId.equals(currentCommitBlobId)){
            // no change
            stagingArea.unstage(fileName);
        }
        else {
            stagingArea.addingFiles(fileName,blobId);
        }

        stagingArea.saveStagingContent();


    }
}
