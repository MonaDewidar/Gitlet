package test;

import gitlet.repository.Blob;
import gitlet.repository.Branch;
import gitlet.repository.Commit;
import gitlet.services.AddCommand;
import gitlet.services.BranchCommand;
import gitlet.services.CheckoutCommand;
import gitlet.services.CommitCommand;
import gitlet.services.InitCommand;
import gitlet.services.MergeCommand;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MergeCommandTest {

    @Before
    public void setUp() {
        TestUtils.cleanRepository("mine.txt", "theirs.txt", "shared.txt");
        InitCommand.init();
    }

    /** A merge brings over the other branch's change and keeps the current branch's change. */
    @Test
    public void takesTheirChangeAndKeepsMine() {
        TestUtils.writeFile("mine.txt", "mine v1");
        AddCommand.add("mine.txt");
        TestUtils.writeFile("theirs.txt", "theirs v1");
        AddCommand.add("theirs.txt");
        CommitCommand.commit("split point");

        BranchCommand.createBranch("other");
        CheckoutCommand.checkoutBranch("other");
        TestUtils.writeFile("theirs.txt", "theirs v2");
        AddCommand.add("theirs.txt");
        CommitCommand.commit("change on other");

        CheckoutCommand.checkoutBranch("master");
        TestUtils.writeFile("mine.txt", "mine v2");
        AddCommand.add("mine.txt");
        CommitCommand.commit("change on master");

        MergeCommand.merge("other");

        assertEquals("theirs v2", TestUtils.readFile("theirs.txt"));
        assertEquals("mine v2", TestUtils.readFile("mine.txt"));
    }

    /** A file changed differently on both branches gets conflict markers, and that content is committed. */
    @Test
    public void writesConflictMarkers() {
        TestUtils.writeFile("shared.txt", "base\n");
        AddCommand.add("shared.txt");
        CommitCommand.commit("split point");

        BranchCommand.createBranch("other");
        CheckoutCommand.checkoutBranch("other");
        TestUtils.writeFile("shared.txt", "given version\n");
        AddCommand.add("shared.txt");
        CommitCommand.commit("change on other");

        CheckoutCommand.checkoutBranch("master");
        TestUtils.writeFile("shared.txt", "current version\n");
        AddCommand.add("shared.txt");
        CommitCommand.commit("change on master");

        MergeCommand.merge("other");

        String expected = "<<<<<<< HEAD\n"
                + "current version\n"
                + "=======\n"
                + "given version\n"
                + ">>>>>>>\n";
        assertEquals(expected, TestUtils.readFile("shared.txt"));

        String blobId = Commit.loadCommit(Branch.currentCommitId()).getBlobs().get("shared.txt");
        assertEquals(expected, Blob.readContent(blobId));
    }

    /** The merge commit has both heads as parents, and the current branch stays master. */
    @Test
    public void mergeCommitHasTwoParents() {
        TestUtils.writeFile("mine.txt", "mine v1");
        AddCommand.add("mine.txt");
        CommitCommand.commit("split point");

        BranchCommand.createBranch("other");
        CheckoutCommand.checkoutBranch("other");
        TestUtils.writeFile("theirs.txt", "theirs v1");
        AddCommand.add("theirs.txt");
        CommitCommand.commit("change on other");
        String otherHeadId = Branch.readBranch("other");

        CheckoutCommand.checkoutBranch("master");
        TestUtils.writeFile("mine.txt", "mine v2");
        AddCommand.add("mine.txt");
        CommitCommand.commit("change on master");
        String masterHeadId = Branch.readBranch("master");

        MergeCommand.merge("other");

        Commit mergeCommit = Commit.loadCommit(Branch.readBranch("master"));
        assertEquals(masterHeadId, mergeCommit.getParent());
        assertEquals(otherHeadId, mergeCommit.getSecondParent());
        assertEquals("Merged other into master.", mergeCommit.getMessage());
        assertEquals("master", Branch.readHead());
    }
}
