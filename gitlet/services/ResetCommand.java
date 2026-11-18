package gitlet.services;

import gitlet.GitletException;
import gitlet.repository.Branch;
import gitlet.repository.Commit;
import gitlet.repository.StagingArea;
import gitlet.repository.WorkingDirectory;

import java.util.List;
import java.util.TreeMap;

public class ResetCommand {
    public static void restCommand(String commitIdAp){
        String commitId=commitIdAp;
        List<String>commits=Commit.listCommit();
        for(String id:commits){
            if(id.startsWith(commitIdAp)){
                commitId=id;
                break;
            }
        }
        Commit commit=Commit.loadCommit(commitId);
        TreeMap<String,String>blobs=commit.getBlobs();
        StagingArea stagingArea=StagingArea.loadStagingContent();
        TreeMap<String,String>currentBlobs=Commit.loadCommit(Branch.readBranch(Branch.readHead())).getBlobs();

        List<String>WDFiles=WorkingDirectory.loadFilesInWD();
        for(String file:WDFiles){
            boolean tracked=currentBlobs.containsKey(file);
            if(!tracked && blobs.containsKey(file)){
                throw new GitletException("There is an untracked file in the way; delete it, or add and commit it first.");
            }
        }

        for(String fileName:currentBlobs.keySet()){
            if(!(blobs.containsKey(fileName))){
                WorkingDirectory.deleteFilesFromWD(fileName);
            }
        }

        for(String fileName:blobs.keySet()){
            CheckoutCommand.checkoutFileWithSpecificCommitId(commitId,fileName);
        }

        Branch.writeBranch(Branch.readHead(),commitId);
        stagingArea.clear();
        stagingArea.saveStagingContent();


    }
}
