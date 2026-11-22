package test;

import gitlet.GitletException;
import gitlet.repository.Branch;
import gitlet.repository.StagingArea;
import gitlet.services.AddCommand;
import gitlet.services.CheckoutCommand;
import gitlet.services.CommitCommand;
import gitlet.services.InitCommand;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class CheckoutCommandTest {

    @Before
    public void setUp() {
        TestUtils.cleanRepository("a.txt", "b.txt");
        InitCommand.init();
        TestUtils.writeFile("a.txt", "version1");
        AddCommand.add("a.txt");
        CommitCommand.commit("first");
    }

    @Test
    public void restoresTheCommittedVersion() {
        TestUtils.writeFile("a.txt", "version2");
        CheckoutCommand.checkoutFile("a.txt");
        assertEquals("version1", TestUtils.readFile("a.txt"));
    }

    /** Checking out a file does not change the staging area: b.txt is still the only staged file. */
    @Test
    public void doesNotChangeTheStagingArea() {
        TestUtils.writeFile("b.txt", "secondFile");
        AddCommand.add("b.txt");
        TestUtils.writeFile("a.txt", "version2");

        CheckoutCommand.checkoutFile("a.txt");
        assertEquals(1, StagingArea.load().getStagedFiles().size());
    }

    /** Checking out a file from an older commit restores it, and master does not move. */
    @Test
    public void restoresAFileFromAnOlderCommit() {
        String firstCommitId = Branch.currentCommitId();
        TestUtils.writeFile("a.txt", "version2");
        AddCommand.add("a.txt");
        CommitCommand.commit("second");
        String secondCommitId = Branch.currentCommitId();

        CheckoutCommand.checkoutFileFromCommit(firstCommitId, "a.txt");

        assertEquals("version1", TestUtils.readFile("a.txt"));
        assertEquals(secondCommitId, Branch.currentCommitId());
    }

    @Test(expected = GitletException.class)
    public void missingFileFails() {
        CheckoutCommand.checkoutFile("mona.txt");
    }

    /** A made-up commit id fails, and a.txt does not change. */
    @Test
    public void wrongCommitIdFails() {
        try {
            CheckoutCommand.checkoutFileFromCommit("wwhjgyu2gwyu2iwuu2oiwu2o", "a.txt");
            fail("expected a GitletException");
        } catch (GitletException e) {
            // expected
        }
        assertEquals("version1", TestUtils.readFile("a.txt"));
    }
}
