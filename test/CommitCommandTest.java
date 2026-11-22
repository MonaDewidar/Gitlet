package test;

import gitlet.GitletException;
import gitlet.repository.Branch;
import gitlet.repository.Commit;
import gitlet.repository.StagingArea;
import gitlet.services.AddCommand;
import gitlet.services.CommitCommand;
import gitlet.services.InitCommand;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class CommitCommandTest {

    @Before
    public void setUp() {
        TestUtils.cleanRepository("a.txt", "b.txt");
        InitCommand.init();
    }

    @Test(expected = GitletException.class)
    public void commitWithNothingStagedFails() {
        CommitCommand.commit("first commit");
    }

    /** A new commit keeps the parent's files and adds the staged ones. */
    @Test
    public void newCommitKeepsTheParentFiles() {
        TestUtils.writeFile("a.txt", "ManMonaa first file");
        AddCommand.add("a.txt");
        CommitCommand.commit("first commit");
        String firstBlobId = Commit.loadCommit(Branch.currentCommitId()).getBlobs().get("a.txt");

        TestUtils.writeFile("b.txt", "ManMonaa second file");
        AddCommand.add("b.txt");
        CommitCommand.commit("second commit");
        Commit secondCommit = Commit.loadCommit(Branch.currentCommitId());

        assertEquals(firstBlobId, secondCommit.getBlobs().get("a.txt"));
        assertEquals(2, secondCommit.getBlobs().size());
    }

    /** After a commit, the branch points to it, its parent is the previous commit, and nothing is staged. */
    @Test
    public void commitMovesTheBranchAndClearsTheStagingArea() {
        String parentId = Branch.currentCommitId();

        TestUtils.writeFile("a.txt", "ManMonaa first file");
        AddCommand.add("a.txt");
        CommitCommand.commit("first commit");
        String newId = Branch.currentCommitId();

        assertNotEquals(parentId, newId);
        assertEquals(parentId, Commit.loadCommit(newId).getParent());
        assertTrue(StagingArea.load().isEmpty());
    }
}
