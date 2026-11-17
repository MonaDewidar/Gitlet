package gitlet.services;

import gitlet.GitletException;
import gitlet.Utils;
import gitlet.repository.*;

public class RmCommand {
    public static void removeCommand(String fileName){
        // if it is staged -> unstage it
        StagingArea stagingArea=StagingArea.loadStagingContent();
        // get the current commit
        String branch= Branch.readHead();
        String commitId=Branch.readBranch(branch);
        Commit commit= Commit.loadCommit(commitId);

        boolean staged=stagingArea.getStagingFiles().containsKey(fileName);
        boolean tracked=commit.getBlobs().containsKey(fileName);


        if(staged){
            stagingArea.unstage(fileName);
        }

        if(tracked){
            // add it in removal and remove it from WD so that next commit will not see it again
            // no changes in the current commit , it is just a note for the next commit
            stagingArea.removingFiles(fileName);
            WorkingDirectory.deleteFilesFromWD(fileName);
        }



        if(!staged && !tracked){
            throw new GitletException("No reason to remove the file.");
        }

        // save any changes in the stagingArea
        stagingArea.saveStagingContent();

    }
}
