package test;

import gitlet.GitletException;
import gitlet.repository.Branch;
import gitlet.repository.Commit;
import gitlet.repository.Path;
import gitlet.services.InitCommand;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InitTestCommand {
    @Before
    public void clean(){
        deleteFolder(Path.GITLET_DIR);
    }

// folder.delete() only delete the folder if it is empty
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

    @Test
    public void whatAreInGitletDirectoryAfterInit(){
        InitCommand.init();
        assertTrue(Path.GITLET_DIR.exists() && Path.Branch_DIR.exists() &&
                Path.COMMIT_DIR.exists()) ;
    }

    @Test
    public void headPointsToMasterAfterInit(){
        InitCommand.init();
        assertEquals("master", Branch.readHead());
    }


    @Test
    public void thereIsOnlyOneCommit(){
        InitCommand.init();
        assertEquals(1,Commit.listCommit().size());
    }

    @Test
    public void branchIsTheMasterAfterInit(){
        InitCommand.init();
        String commit_id=Branch.readBranch("master");
        String onlyOneCommit= Commit.listCommit().get(0);
        assertEquals(commit_id,onlyOneCommit);
    }

    @Test(expected = GitletException.class)
    public void runInitTwice(){
        InitCommand.init();
        InitCommand.init();

    }

}
