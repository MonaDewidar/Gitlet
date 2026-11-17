package test;

import gitlet.GitletException;
import gitlet.Utils;
import gitlet.repository.Path;
import gitlet.repository.StagingArea;
import gitlet.repository.WorkingDirectory;
import gitlet.services.AddCommand;
import gitlet.services.CommitCommand;
import gitlet.services.InitCommand;
import gitlet.services.RmCommand;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RmTestCommand {

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
    // create a.txt -> rm it , removed from the stagingfiles , added to removed set, still in WD
    @Test
    public void creatingFilesWithoutCommitedThenDeleteIt(){
        InitCommand.init();
        Utils.writeContents(Utils.join(Path.CWD,"a.txt"),"ManMonaa first file");
        AddCommand.addingFunction("a.txt");
        RmCommand.removeCommand("a.txt");
        StagingArea stagingArea=StagingArea.loadStagingContent();
        boolean flag=stagingArea.getStagingFiles().containsKey("a.txt");
        assertFalse(flag);

        flag=stagingArea.getStagingRemove().contains("a.txt");
        assertFalse(flag);

        flag =Utils.join(Path.CWD,"a.txt").exists();
        assertTrue(flag);
    }


    //Removing a committed file stages it for removal and deletes it from the working directory.
    @Test
    public void creatingFilesAfterCommitedThenDeleteIt(){
        InitCommand.init();
        Utils.writeContents(Utils.join(Path.CWD,"a.txt"),"ManMonaa first file");
        AddCommand.addingFunction("a.txt");
        CommitCommand.addCommit("first commit");
        RmCommand.removeCommand("a.txt");
        StagingArea stagingArea=StagingArea.loadStagingContent();
        boolean flag=stagingArea.getStagingFiles().containsKey("a.txt");
        assertFalse(flag);

        flag=stagingArea.getStagingRemove().contains("a.txt");
        assertTrue(flag);

        flag =Utils.join(Path.CWD,"a.txt").exists();
        assertFalse(flag);
    }



    //Removing a file that is neither staged nor tracked is refused and leaves the file alone.
    @Test(expected = GitletException.class)
    public void justCreateTheFile(){
        InitCommand.init();
        Utils.writeContents(Utils.join(Path.CWD,"a.txt"),"ManMonaa first file");
        RmCommand.removeCommand("a.txt");
    }
}
