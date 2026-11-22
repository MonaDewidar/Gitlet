package test;

import gitlet.GitletException;
import gitlet.repository.Branch;
import gitlet.repository.StagingArea;
import gitlet.repository.WorkingDirectory;
import gitlet.services.AddCommand;
import gitlet.services.CommitCommand;
import gitlet.services.InitCommand;
import gitlet.services.ResetCommand;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ResetCommandTest {

    @Before
    public void setUp() {
        TestUtils.cleanRepository("a.txt", "b.txt");
        InitCommand.init();
    }

    /** Resetting to an older commit restores its files and deletes the newer ones. */
    @Test
    public void restoresOldFilesAndRemovesNewerOnes() {
        TestUtils.writeFile("a.txt", "version1");
        AddCommand.add("a.txt");
        CommitCommand.commit("first");
        String firstCommitId = Branch.currentCommitId();

        TestUtils.writeFile("a.txt", "version2");
        TestUtils.writeFile("b.txt", "new file");
        AddCommand.add("b.txt");
        CommitCommand.commit("second");

        ResetCommand.reset(firstCommitId);

        assertEquals("version1", TestUtils.readFile("a.txt"));
        assertFalse(WorkingDirectory.listFiles().contains("b.txt"));
    }

    /** Reset moves the current branch, keeps HEAD on it, and clears the staging area. */
    @Test
    public void movesTheBranchAndClearsTheStagingArea() {
        TestUtils.writeFile("a.txt", "version1");
        AddCommand.add("a.txt");
        CommitCommand.commit("first");
        String firstCommitId = Branch.currentCommitId();

        TestUtils.writeFile("b.txt", "new file");
        AddCommand.add("b.txt");
        CommitCommand.commit("second");

        ResetCommand.reset(firstCommitId);

        assertEquals(firstCommitId, Branch.currentCommitId());
        assertEquals("master", Branch.readHead());
        assertTrue(StagingArea.load().isEmpty());
    }

    /** An untracked file that would be overwritten stops the reset, and nothing changes. */
    @Test
    public void untrackedFileInTheWayStopsTheReset() {
        TestUtils.writeFile("a.txt", "version1");
        AddCommand.add("a.txt");
        CommitCommand.commit("first");
        String firstCommitId = Branch.currentCommitId();

        TestUtils.writeFile("b.txt", "new file");
        AddCommand.add("b.txt");
        CommitCommand.commit("second");
        String secondCommitId = Branch.currentCommitId();

        ResetCommand.reset(firstCommitId);
        TestUtils.writeFile("b.txt", "my own b.txt");

        try {
            ResetCommand.reset(secondCommitId);
            fail("expected a GitletException");
        } catch (GitletException e) {
            // expected
        }
        assertEquals(firstCommitId, Branch.currentCommitId());
        assertEquals("my own b.txt", TestUtils.readFile("b.txt"));
    }
}
