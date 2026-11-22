package test;

import gitlet.GitletException;
import gitlet.repository.Blob;
import gitlet.repository.StagingArea;
import gitlet.services.AddCommand;
import gitlet.services.InitCommand;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class AddCommandTest {

    @Before
    public void setUp() {
        TestUtils.cleanRepository("a.txt", "b.txt");
        InitCommand.init();
    }

    /** The staged blob holds the same content as the file. */
    @Test
    public void storesTheSameContent() {
        TestUtils.writeFile("a.txt", "ManMonaa");
        AddCommand.add("a.txt");

        String blobId = StagingArea.load().getStagedFiles().get("a.txt");
        assertEquals("ManMonaa", Blob.readContent(blobId));
    }

    @Test
    public void addingTwoFilesStagesBoth() {
        TestUtils.writeFile("a.txt", "changed");
        AddCommand.add("a.txt");
        TestUtils.writeFile("b.txt", "new file");
        AddCommand.add("b.txt");

        assertEquals(2, StagingArea.load().getStagedFiles().size());
    }

    /** Adding the same file again replaces the staged version: one file, a new blob id. */
    @Test
    public void addingTheSameFileAgainKeepsTheLatestVersion() {
        TestUtils.writeFile("a.txt", "first content");
        AddCommand.add("a.txt");
        String firstId = StagingArea.load().getStagedFiles().get("a.txt");

        TestUtils.writeFile("a.txt", "second content");
        AddCommand.add("a.txt");
        String secondId = StagingArea.load().getStagedFiles().get("a.txt");

        assertEquals(1, StagingArea.load().getStagedFiles().size());
        assertNotEquals(firstId, secondId);
    }

    @Test(expected = GitletException.class)
    public void addingAMissingFileFails() {
        AddCommand.add("b.txt");
    }
}
