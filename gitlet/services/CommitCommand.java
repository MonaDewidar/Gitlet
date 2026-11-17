package gitlet.services;

import gitlet.GitletException;
import gitlet.Utils;
import gitlet.repository.Branch;
import gitlet.repository.Commit;
import gitlet.repository.Path;
import gitlet.repository.StagingArea;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.TreeMap;

public class CommitCommand {

    public static  void addCommit(String message){
        StagingArea stagingArea=StagingArea.loadStagingContent();
        // is staging area empty
        if(stagingArea.getStagingFiles().size()==0 && stagingArea.getStagingRemove().size()==0){
            throw new GitletException("No changes added to the commit.");
        }


        // get the current commit-> parent -> copy its -> change it by the current staging area
        String branch= Branch.readHead();
        String commitId=Branch.readBranch(branch);
        Commit currentCommit=Commit.loadCommit(commitId);

        TreeMap<String,String>newBlobs=new TreeMap<>(currentCommit.getBlobs());
        // now check diff between them
        newBlobs.putAll(stagingArea.getStagingFiles());
        for(String curr:stagingArea.getStagingRemove()){
            newBlobs.remove(curr);
        }
        Commit nextCommit=new Commit(message,new Date(),commitId,null,newBlobs);

        String id=nextCommit.saveCommit();
        Branch.writeBranch(branch,id);
        stagingArea.clear(); // clear the object
        stagingArea.saveStagingContent(); // clear from disk

    }







}
