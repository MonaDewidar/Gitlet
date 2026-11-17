package test;

import gitlet.GitletException;
import gitlet.Utils;
import gitlet.repository.Blob;
import gitlet.repository.Path;
import gitlet.repository.StagingArea;
import gitlet.services.AddCommand;
import gitlet.services.InitCommand;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AddTestCommand {

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



    // test it stores the same content
    @Test
    public void storeTheSameContent(){
        Utils.writeContents(Utils.join(Path.CWD,"a.txt"),"ManMonaa");
        AddCommand.addingFunction("a.txt");
        String id= StagingArea.loadStagingContent().getStagingFiles().get("a.txt");
        String content= Blob.readContent(id);
        assertEquals("ManMonaa",content);
    }

    // test another 2 files
    @Test
    public void creatNewFileWithDifferentContent(){
        Utils.writeContents(Utils.join(Path.CWD,"a.txt"),"changed");
        AddCommand.addingFunction("a.txt");

        Utils.writeContents(Utils.join(Path.CWD,"b.txt"),"new file");
        AddCommand.addingFunction("b.txt");

        int cnt=StagingArea.loadStagingContent().getStagingFiles().size();
        assertEquals(cnt,2);

    }

    // editing the same file -> 2 id , 1 file
    @Test
    public void changeTheSameFile(){
        Utils.writeContents(Utils.join(Path.CWD,"a.txt"),"first content");
        AddCommand.addingFunction("a.txt");
        String id1=StagingArea.loadStagingContent().getStagingFiles().get("a.txt");

        Utils.writeContents(Utils.join(Path.CWD,"a.txt"),"second content");
        AddCommand.addingFunction("a.txt");
        String id2=StagingArea.loadStagingContent().getStagingFiles().get("a.txt");

        int sz=StagingArea.loadStagingContent().getStagingFiles().size();
        boolean flag=(sz==1) && (!(id1.equals(id2)));
        assertTrue(flag);
    }

    // file not exist
    @Test(expected = GitletException.class)
    public void fileNotExist(){
        AddCommand.addingFunction("b.txt");
    }
}
