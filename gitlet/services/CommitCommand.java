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




    public static void addCommit(String message){          // NEW (3 lines): keeps the old call working
        addCommit(message, null);                          // NEW
    }                                                      // NEW

    public static void addCommit(String message, String secondParent){   // EDITED: second parameter added
        StagingArea stagingArea=StagingArea.loadStagingContent();
        if(stagingArea.getStagingFiles().size()==0 && stagingArea.getStagingRemove().size()==0){
            throw new GitletException("No changes added to the commit.");
        }
        if(message.equals("")){
            throw new GitletException("Please enter a commit message.");
        }

        String branch= Branch.readHead();
        String commitId=Branch.readBranch(branch);
        Commit currentCommit=Commit.loadCommit(commitId);

        TreeMap<String,String> newBlobs = new TreeMap<>(currentCommit.getBlobs());
        newBlobs.putAll(stagingArea.getStagingFiles());
        for(String curr : stagingArea.getStagingRemove()){
            newBlobs.remove(curr);
        }

        Commit nextCommit = new Commit(message, new Date(), commitId, secondParent, newBlobs);  // EDITED: null -> secondParent
        String id = nextCommit.saveCommit();
        Branch.writeBranch(branch, id);
        stagingArea.clear();
        stagingArea.saveStagingContent();
    }







}
