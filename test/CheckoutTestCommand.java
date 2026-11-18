package test;

import gitlet.GitletException;
import gitlet.Utils;
import gitlet.repository.*;
import gitlet.services.AddCommand;
import gitlet.services.CheckoutCommand;
import gitlet.services.CommitCommand;
import gitlet.services.InitCommand;
import jdk.jshell.execution.Util;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class CheckoutTestCommand {

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
    public void returnThePreviousVersion(){
        InitCommand.init();
        Utils.writeContents(Utils.join(Path.CWD,"a.txt"),"version1");
        AddCommand.addingFunction("a.txt");
        CommitCommand.addCommit("first");
        String content1=Utils.readContentsAsString(Utils.join(Path.CWD,"a.txt"));
        Utils.writeContents(Utils.join(Path.CWD,"a.txt"),"version2");
        CheckoutCommand.checkoutFile("a.txt");
        String content2=Utils.readContentsAsString(Utils.join(Path.CWD,"a.txt"));
        assertEquals(content2,content1);

    }

    //Commit a.txt, stage a different file b.txt,
    // change a.txt and check it out. The staging area still contains b.txt and nothing else.

    @Test
    public void thisCommandDoesNotChangeStagingArea(){
        InitCommand.init();
        Utils.writeContents(Utils.join(Path.CWD,"a.txt"),"version1");
        AddCommand.addingFunction("a.txt");
        CommitCommand.addCommit("first");
        Utils.writeContents(Utils.join(Path.CWD,"b.txt"),"secondFile");
        AddCommand.addingFunction("b.txt");
        Utils.writeContents(Utils.join(Path.CWD,"a.txt"),"version2");
        CheckoutCommand.checkoutFile("a.txt");
        assertEquals(1, StagingArea.loadStagingContent().getStagingFiles().size());

    }

//    Commit a.txt with version1 and remember that commit’s id,
//    then commit it again with version2. Check out the file using the first id.
//    The content is version1, and master still points to the second commit.

    @Test
    public void checkoutWithCommitId(){
        InitCommand.init();
        Utils.writeContents(Utils.join(Path.CWD,"a.txt"),"version1");
        AddCommand.addingFunction("a.txt");
        CommitCommand.addCommit("first");
        String firstCommitId= Branch.readBranch(Branch.readHead());
        String content1=Utils.readContentsAsString(Utils.join(Path.CWD,"a.txt"));
        Utils.writeContents(Utils.join(Path.CWD,"a.txt"),"version2");
        AddCommand.addingFunction("a.txt");
        CommitCommand.addCommit("second");
        String commitId2=Branch.readBranch(Branch.readHead());
        CheckoutCommand.checkoutFileWithSpecificCommitId(firstCommitId,"a.txt");
        String content2=Utils.readContentsAsString(Utils.join(Path.CWD,"a.txt"));
        String commitId3=Branch.readBranch(Branch.readHead());
        assertEquals(content2,content1);

        assertEquals(commitId3,commitId2);
    }

    //Commit a.txt, then check out nothing.txt. A GitletException is thrown.
    @Test(expected = GitletException.class)
    public void noFileExist(){
        InitCommand.init();
        Utils.writeContents(Utils.join(Path.CWD,"a.txt"),"version1");
        AddCommand.addingFunction("a.txt");
        CommitCommand.addCommit("first");
        CheckoutCommand.checkoutFile("mona.txt");
    }


    //Check out a.txt from a made-up id. A GitletException is thrown,
    // and a.txt in the working directory is unchanged.

    @Test
    public void wrongCommitId(){
        InitCommand.init();
        Utils.writeContents(Utils.join(Path.CWD,"a.txt"),"version1");
        AddCommand.addingFunction("a.txt");
        CommitCommand.addCommit("first");
        try {
            CheckoutCommand.checkoutFileWithSpecificCommitId("wwhjgyu2gwyu2iwuu2oiwu2o", "a.txt");
            fail("expected a GitletException");
        }catch (GitletException e){

        }

        String content2=Utils.readContentsAsString(Utils.join(Path.CWD,"a.txt"));
        assertEquals("version1",content2);
    }

}
