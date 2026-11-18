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
        Commit commit=Commit.loadCommit(commitId);
      if(commit.getBlobs().containsKey(fileName)){
          // read the content of this file
          String content= Utils.readContentsAsString(Utils.join(Path.BLOB_DIR,commit.getBlobs().get(fileName)));
          Utils.writeContents(Utils.join(Path.CWD,fileName),content);
        }
        else {
            throw new GitletException("File does not exist in that commit.");
        }
    }

    public static void checkoutFileWithSpecificCommitId(String commitIdAp,String fileName){
        String commitId=commitIdAp;
        List<String>commits=Commit.listCommit();
        for(String id:commits){
            if(id.startsWith(commitIdAp)){
                commitId=id;
                break;
            }
        }

        Commit commit=Commit.loadCommit(commitId);
        if (commit.getBlobs().containsKey(fileName)) {
            // read the content of this file
            String content = Utils.readContentsAsString(Utils.join(Path.BLOB_DIR, commit.getBlobs().get(fileName)));
            Utils.writeContents(Utils.join(Path.CWD, fileName), content);
        } else {
            throw new GitletException("File does not exist in that commit.");
        }
    }


    public static void checkoutBranch(String branchName){
     List<String>branches=Utils.plainFilenamesIn(Path.Branch_DIR);
     if(!(branches.contains(branchName))){
         throw new GitletException("No such branch exists.");
     }
    String currentBranch=Branch.readHead();
    if(currentBranch.equals(branchName)){
        throw new GitletException("No need to checkout the current branch.");
    }
//    If a working file is untracked in the current branch and would be overwritten by the checkout,
//    print There is an untracked file in the way; delete it, or add and commit it first
     // get all blobs
     Commit currentCommit=Commit.loadCommit(Branch.readBranch(Branch.readHead()));
    Commit branchCommit=Commit.loadCommit(Branch.readBranch(branchName));
     TreeMap<String,String> currentBlob=currentCommit.getBlobs();
     TreeMap<String,String> branchBlob=branchCommit.getBlobs();
     List<String>WDFiles= WorkingDirectory.loadFilesInWD();
     for(String file:WDFiles){
         boolean tracked=currentBlob.containsKey(file);
         if(!tracked && branchBlob.containsKey(file)){
             throw new GitletException("There is an untracked file in the way; delete it, or add and commit it first.");
         }
     }

     StagingArea stagingArea=StagingArea.loadStagingContent();
     // overwrite all current commit files
     for(String file:branchBlob.keySet()) {
         String content = Utils.readContentsAsString(Utils.join(Path.BLOB_DIR, branchCommit.getBlobs().get(file)));
         Utils.writeContents(Utils.join(Path.CWD, file), content);
     }

     for(String file:currentBlob.keySet()){
         if(!(branchBlob.containsKey(file))){
             WorkingDirectory.deleteFilesFromWD(file);
         }
     }
     stagingArea.clear();
     stagingArea.saveStagingContent();
     Branch.writeHead(branchName);

    }
}
