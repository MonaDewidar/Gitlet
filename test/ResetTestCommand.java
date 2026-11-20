package test;

import gitlet.GitletException;
import gitlet.Utils;
import gitlet.repository.Branch;
import gitlet.repository.Path;
import gitlet.repository.StagingArea;
import gitlet.repository.WorkingDirectory;
import gitlet.services.AddCommand;
import gitlet.services.CommitCommand;
import gitlet.services.InitCommand;
import gitlet.services.ResetCommand;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class ResetTestCommand {

    @Before
    public void clean(){
        deleteFolder(Path.GITLET_DIR);
        Utils.join(Path.CWD,"a.txt").delete();
        Utils.join(Path.CWD,"b.txt").delete();
        InitCommand.init();
    }

    /// folder.delete() only delete the folder if it is empty
    void deleteFolder(File folder){
        File[] in=folder.listFiles();
        if(in !=null){
            for(File F:in){
                if(F.isDirectory()){
                    deleteFolder(F);
                }
                else {
                    F.delete();
                }
            }
        }
        folder.delete();
    }

    //Commit a.txt with v1 and remember that commit’s id. Then change a.txt to v2, add a new file b.txt,
    // and commit both. Reset to the first id. a.txt contains v1,
    // and b.txt no longer exists in the working directory.

    @Test
    public void  resettingToAnOlderCommitRestoresItsFilesAndRemovesNewerOnes(){
        Utils.writeContents(Utils.join(Path.CWD,"a.txt"),"version1");
        AddCommand.addingFunction("a.txt");
        CommitCommand.addCommit("first");
        String commitId= Branch.readBranch(Branch.readHead());
        Utils.writeContents(Utils.join(Path.CWD,"a.txt"),"version2");
        Utils.writeContents(Utils.join(Path.CWD,"b.txt"),"new file");
        AddCommand.addingFunction("b.txt");
        CommitCommand.addCommit("second");
        ResetCommand.restCommand(commitId);
        String content=Utils.readContentsAsString(Utils.join(Path.CWD,"a.txt"));
        assertEquals("version1",content);

        assertFalse(WorkingDirectory.loadFilesInWD().contains("b.txt"));

    }


    //Make two commits and remember the first one’s id. Stage a new file,
    // then reset to the first id. master holds the first id,
    // HEAD still says master, and the staging area’s map and set are both empty.

    @Test
    public void resetMovesTheCurrentBranchHeadNotStagingAreaCleared(){
        Utils.writeContents(Utils.join(Path.CWD,"a.txt"),"version1");
        AddCommand.addingFunction("a.txt");
        CommitCommand.addCommit("first");
        String commitId= Branch.readBranch(Branch.readHead());
        Utils.writeContents(Utils.join(Path.CWD,"b.txt"),"new file");
        AddCommand.addingFunction("b.txt");
        CommitCommand.addCommit("second");
        ResetCommand.restCommand(commitId);
        assertEquals(Branch.readBranch(Branch.readHead()),commitId);
        assertEquals("master",Branch.readHead());
        assertTrue(StagingArea.loadStagingContent().getStagingFiles().isEmpty());
        assertTrue(StagingArea.loadStagingContent().getStagingRemove().isEmpty());
    }

    //Commit a.txt and remember the id. Add b.txt and commit, and remember that id too. Reset to the first id,
    // then create your own b.txt with different content, and reset to the second id.
    // A GitletException is thrown,
    // b.txt still has your content, and master still holds the first id

    @Test
    public void untrackedFilesThatWillBeOverwrittenStopTheCommand(){
        Utils.writeContents(Utils.join(Path.CWD,"a.txt"),"version1");
        AddCommand.addingFunction("a.txt");
        CommitCommand.addCommit("first");
        String commitId1= Branch.readBranch(Branch.readHead());
        Utils.writeContents(Utils.join(Path.CWD,"b.txt"),"new file");
        AddCommand.addingFunction("b.txt");
        CommitCommand.addCommit("second");
        String commitId2= Branch.readBranch(Branch.readHead());
        ResetCommand.restCommand(commitId1);
        Utils.writeContents(Utils.join(Path.CWD,"b.txt"),"new fileeeeeeeeeeeeeee");

        try{
            ResetCommand.restCommand(commitId2);
            fail("expect GitletException");
        }catch(GitletException e){
        }
        assertEquals(Branch.readBranch(Branch.readHead()),commitId1);
        assertEquals(Utils.readContentsAsString(Utils.join(Path.CWD,"b.txt")),"new fileeeeeeeeeeeeeee");

    }

}
