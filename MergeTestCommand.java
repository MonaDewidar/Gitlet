

import gitlet.Utils;
import gitlet.repository.Blob;
import gitlet.repository.Branch;
import gitlet.repository.Commit;
import gitlet.repository.Path;
import gitlet.services.*;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class MergeTestCommand {

    @Before
    public void clean(){
        deleteFolder(Path.GITLET_DIR);
        Utils.join(Path.CWD,"mine.txt").delete();
        Utils.join(Path.CWD,"theirs.txt").delete();
        Utils.join(Path.CWD,"shared.txt").delete();
        InitCommand.init();
    }

    // folder.delete() only deletes the folder if it is empty
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

    // helpers, so the tests stay readable
    void writeFile(String name, String content){
        Utils.writeContents(Utils.join(Path.CWD,name),content);
    }

    String readFile(String name){
        return Utils.readContentsAsString(Utils.join(Path.CWD,name));
    }


    // 1. a merge brings over the other branch's change and keeps your own
    @Test
    public void mergeTakesTheirChangeAndKeepsMine(){
        writeFile("mine.txt","mine v1");
        AddCommand.addingFunction("mine.txt");
        writeFile("theirs.txt","theirs v1");
        AddCommand.addingFunction("theirs.txt");
        CommitCommand.addCommit("split point");

        BranchCommand.branchCommand("other");
        CheckoutCommand.checkoutBranch("other");
        writeFile("theirs.txt","theirs v2");
        AddCommand.addingFunction("theirs.txt");
        CommitCommand.addCommit("change on other");

        CheckoutCommand.checkoutBranch("master");
        writeFile("mine.txt","mine v2");
        AddCommand.addingFunction("mine.txt");
        CommitCommand.addCommit("change on master");

        MergeCommand.mergeCommand("other");

        assertEquals("theirs v2", readFile("theirs.txt"));   // their change came over
        assertEquals("mine v2", readFile("mine.txt"));       // my change stayed
    }


    // 2. the same file changed on both sides ends up with conflict markers
    @Test
    public void mergeWritesConflictMarkers(){
        writeFile("shared.txt","base\n");
        AddCommand.addingFunction("shared.txt");
        CommitCommand.addCommit("split point");

        BranchCommand.branchCommand("other");
        CheckoutCommand.checkoutBranch("other");
        writeFile("shared.txt","given version\n");
        AddCommand.addingFunction("shared.txt");
        CommitCommand.addCommit("change on other");

        CheckoutCommand.checkoutBranch("master");
        writeFile("shared.txt","current version\n");
        AddCommand.addingFunction("shared.txt");
        CommitCommand.addCommit("change on master");

        MergeCommand.mergeCommand("other");

        String expected = "<<<<<<< HEAD\n"
                + "current version\n"
                + "=======\n"
                + "given version\n"
                + ">>>>>>>\n";
        assertEquals(expected, readFile("shared.txt"));

        // the conflicted file was staged, so the merge commit tracks that content
        String mergeCommitId = Branch.readBranch(Branch.readHead());
        String blobId = Commit.loadCommit(mergeCommitId).getBlobs().get("shared.txt");
        assertEquals(expected, Blob.readContent(blobId));
    }


    // 3. the merge commit records both heads as parents and the branch moves
    @Test
    public void mergeCommitHasTwoParents(){
        writeFile("mine.txt","mine v1");
        AddCommand.addingFunction("mine.txt");
        CommitCommand.addCommit("split point");

        BranchCommand.branchCommand("other");
        CheckoutCommand.checkoutBranch("other");
        writeFile("theirs.txt","theirs v1");
        AddCommand.addingFunction("theirs.txt");
        CommitCommand.addCommit("change on other");
        String otherHead = Branch.readBranch("other");

        CheckoutCommand.checkoutBranch("master");
        writeFile("mine.txt","mine v2");
        AddCommand.addingFunction("mine.txt");
        CommitCommand.addCommit("change on master");
        String masterHeadBefore = Branch.readBranch("master");

        MergeCommand.mergeCommand("other");

        String mergeCommitId = Branch.readBranch("master");
        Commit mergeCommit = Commit.loadCommit(mergeCommitId);

        assertEquals(masterHeadBefore, mergeCommit.getParent());      // first parent
        assertEquals(otherHead, mergeCommit.getSecParent());          // second parent
        assertEquals("Merged other into master.", mergeCommit.getMessage());
        assertEquals("master", Branch.readHead());                    // still on master
    }
}