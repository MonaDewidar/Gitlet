package test;

import gitlet.GitletException;
import gitlet.Utils;
import gitlet.repository.Branch;
import gitlet.repository.Commit;
import gitlet.repository.Path;
import gitlet.services.AddCommand;
import gitlet.services.CommitCommand;
import gitlet.services.InitCommand;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class CommitTestCommand {

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

    @Test(expected = GitletException.class)
    public void commitWithNothingStaged(){
        InitCommand.init();
        CommitCommand.addCommit("first commit");
    }

    @Test
    public void addingTwoFilesCommitEachOne(){
        InitCommand.init();
        Utils.writeContents(Utils.join(Path.CWD,"a.txt"),"ManMonaa first file");
        AddCommand.addingFunction("a.txt");
        CommitCommand.addCommit("first commit");
//        a.txt blobId
        String branch= Branch.readHead();
        String commitId=Branch.readBranch(branch);
        String blobAId= Commit.loadCommit(commitId).getBlobs().get("a.txt");

        Utils.writeContents(Utils.join(Path.CWD,"b.txt"),"ManMonaa second file");
        AddCommand.addingFunction("b.txt");
        CommitCommand.addCommit("second commit");

        // current blobId of a == previous
        String branch2= Branch.readHead();
        String commitId2=Branch.readBranch(branch2);
        String blobAId2= Commit.loadCommit(commitId2).getBlobs().get("a.txt");
        // current commit cover 2 files
        int cnt=Commit.loadCommit(commitId2).getBlobs().size();
        boolean flag=(blobAId2.equals(blobAId)) && (cnt==2);
        assertTrue(flag);

    }

    ///After a commit, the branch points to it, its parent is the previous commit, and the staging area is empty.
    @Test
    public void afterCommitWhatHappen(){
        InitCommand.init();

        String branch1= Branch.readHead();
        String commitId1=Branch.readBranch(branch1); // old one -> parent


        Utils.writeContents(Utils.join(Path.CWD,"a.txt"),"ManMonaa first file");
        AddCommand.addingFunction("a.txt");
        CommitCommand.addCommit("first commit");
        String branch2= Branch.readHead();
        String commitId2=Branch.readBranch(branch2); // new

        boolean flag =!(commitId2.equals(commitId1)) && (Commit.loadCommit(commitId2).getParent().equals(commitId1));
        assertTrue(flag);


        assertNotEquals(commitId2,commitId1);

        assertEquals(Commit.loadCommit(commitId2).getParent(),commitId1);


    }
}
