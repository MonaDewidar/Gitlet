package gitlet.services;

import gitlet.GitletException;
import gitlet.repository.Branch;
import gitlet.repository.Commit;
import gitlet.repository.StagingArea;

import java.util.Date;
import java.util.TreeMap;

public class CommitCommand {

    public static void addCommit(String message){
        addCommit(message, null);
    }

    // secondParent is null for a normal commit, and the given branch head for a merge commit
    public static void addCommit(String message, String secondParent){
        if(message==null || message.trim().isEmpty()){
            throw new GitletException("Please enter a commit message.");
        }
        StagingArea stagingArea=StagingArea.loadStagingContent();
        if(stagingArea.getStagingFiles().isEmpty() && stagingArea.getStagingRemove().isEmpty()){
            throw new GitletException("No changes added to the commit.");
        }

        String branch= Branch.readHead();
        String commitId=Branch.readBranch(branch);
        Commit currentCommit=Commit.loadCommit(commitId);

        TreeMap<String,String> newBlobs = new TreeMap<>(currentCommit.getBlobs());
        newBlobs.putAll(stagingArea.getStagingFiles());
        for(String curr : stagingArea.getStagingRemove()){
            newBlobs.remove(curr);
        }

        Commit nextCommit = new Commit(message, new Date(), commitId, secondParent, newBlobs);
        String id = nextCommit.saveCommit();
        Branch.writeBranch(branch, id);
        stagingArea.clear();
        stagingArea.saveStagingContent();
    }

}
