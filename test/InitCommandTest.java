package test;

import gitlet.GitletException;
import gitlet.repository.Branch;
import gitlet.repository.Commit;
import gitlet.repository.Path;
import gitlet.services.InitCommand;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InitCommandTest {

    @Before
    public void setUp() {
        TestUtils.cleanRepository();
    }

    @Test
    public void createsTheGitletDirectories() {
        InitCommand.init();
        assertTrue(Path.GITLET_DIR.exists());
        assertTrue(Path.BRANCH_DIR.exists());
        assertTrue(Path.COMMIT_DIR.exists());
    }

    @Test
    public void headPointsToMaster() {
        InitCommand.init();
        assertEquals("master", Branch.readHead());
    }

    @Test
    public void thereIsOnlyOneCommit() {
        InitCommand.init();
        assertEquals(1, Commit.listCommitIds().size());
    }

    @Test
    public void masterPointsToTheInitialCommit() {
        InitCommand.init();
        String initialCommitId = Commit.listCommitIds().get(0);
        assertEquals(initialCommitId, Branch.readBranch("master"));
    }

    @Test(expected = GitletException.class)
    public void initTwiceFails() {
        InitCommand.init();
        InitCommand.init();
    }
}
