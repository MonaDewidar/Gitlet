package gitlet.services;

import gitlet.GitletException;
import gitlet.Utils;
import gitlet.repository.*;

import java.util.List;
import java.util.TreeMap;

public class CheckoutCommand {
    public static void checkoutFile(String fileName){
        String branch= Branch.readHead();
        String commitId=Branch.readBranch(branch);
        checkoutFileFromCommit(commitId,fileName);
    }

    public static void checkoutFileWithSpecificCommitId(String commitIdAp,String fileName){
        // works with the full id or an abbreviated one
        String commitId=Commit.findFullId(commitIdAp);
        checkoutFileFromCommit(commitId,fileName);
    }

    private static void checkoutFileFromCommit(String commitId,String fileName){
        Commit commit=Commit.loadCommit(commitId);
        String blobId=commit.getBlobs().get(fileName);
        if(blobId==null){
            throw new GitletException("File does not exist in that commit.");
        }
        // write the content of this file into the CWD, the staging area does not change
        WorkingDirectory.writeBlobIntoWD(fileName,blobId);
    }


    public static void checkoutBranch(String branchName){
     List<String>branches=Utils.plainFilenamesIn(Path.Branch_DIR);
     if(branches==null || !(branches.contains(branchName))){
         throw new GitletException("No such branch exists.");
     }
    String currentBranch=Branch.readHead();
    if(currentBranch.equals(branchName)){
        throw new GitletException("No need to checkout the current branch.");
    }
     checkoutCommit(Branch.readBranch(branchName));
     Branch.writeHead(branchName);

    }

    // make the CWD the same as this commit and clear the staging area (used by checkout branch and reset)
    static void checkoutCommit(String targetCommitId){
     Commit currentCommit=Commit.loadCommit(Branch.readBranch(Branch.readHead()));
     Commit targetCommit=Commit.loadCommit(targetCommitId);
     TreeMap<String,String> currentBlob=currentCommit.getBlobs();
     TreeMap<String,String> targetBlob=targetCommit.getBlobs();

//    If a working file is untracked in the current branch and would be overwritten,
//    print There is an untracked file in the way; delete it, or add and commit it first
     List<String>WDFiles= WorkingDirectory.loadFilesInWD();
     for(String file:WDFiles){
         boolean tracked=currentBlob.containsKey(file);
         if(!tracked && targetBlob.containsKey(file)){
             throw new GitletException("There is an untracked file in the way; delete it, or add and commit it first.");
         }
     }

     // overwrite all current commit files
     for(String file:targetBlob.keySet()) {
         WorkingDirectory.writeBlobIntoWD(file,targetBlob.get(file));
     }

     for(String file:currentBlob.keySet()){
         if(!(targetBlob.containsKey(file))){
             WorkingDirectory.deleteFilesFromWD(file);
         }
     }
     StagingArea stagingArea=StagingArea.loadStagingContent();
     stagingArea.clear();
     stagingArea.saveStagingContent();
    }
}
