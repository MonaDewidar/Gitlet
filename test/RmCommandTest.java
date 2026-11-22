package test;

import gitlet.GitletException;
import gitlet.Utils;
import gitlet.repository.Path;
import gitlet.repository.StagingArea;
import gitlet.services.AddCommand;
import gitlet.services.CommitCommand;
import gitlet.services.InitCommand;
import gitlet.services.RmCommand;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RmCommandTest {

    @Before
    public void setUp() {
        TestUtils.cleanRepository("a.txt", "b.txt");
        InitCommand.init();
    }

    /** Removing a staged file that is not committed only unstages it and keeps the file. */
    @Test
    public void removingAStagedFileUnstagesIt() {
        TestUtils.writeFile("a.txt", "ManMonaa first file");
        AddCommand.add("a.txt");
        RmCommand.remove("a.txt");

        StagingArea stagingArea = StagingArea.load();
        assertFalse(stagingArea.getStagedFiles().containsKey("a.txt"));
        assertFalse(stagingArea.getRemovedFiles().contains("a.txt"));
        assertTrue(Utils.join(Path.CWD, "a.txt").exists());
    }

    /** Removing a committed file stages it for removal and deletes it from the working directory. */
    @Test
    public void removingACommittedFileStagesItForRemoval() {
        TestUtils.writeFile("a.txt", "ManMonaa first file");
        AddCommand.add("a.txt");
        CommitCommand.commit("first commit");
        RmCommand.remove("a.txt");

        StagingArea stagingArea = StagingArea.load();
        assertFalse(stagingArea.getStagedFiles().containsKey("a.txt"));
        assertTrue(stagingArea.getRemovedFiles().contains("a.txt"));
        assertFalse(Utils.join(Path.CWD, "a.txt").exists());
    }

    /** Removing a file that is neither staged nor tracked fails. */
    @Test(expected = GitletException.class)
    public void removingAnUntrackedFileFails() {
        TestUtils.writeFile("a.txt", "ManMonaa first file");
        RmCommand.remove("a.txt");
    }
}
