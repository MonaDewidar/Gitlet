package gitlet.services;

import gitlet.GitletException;
import gitlet.repository.Branch;
import gitlet.repository.Commit;
import gitlet.repository.Path;
import gitlet.repository.StagingArea;

import java.util.Date;
import java.util.TreeMap;

/** init: creates a new repository with one initial commit on the master branch. */
public class InitCommand {

    public static void init() {
        if (isInitialized()) {
            throw new GitletException(
                    "A Gitlet version-control system already exists in the current directory.");
        }
        Path.COMMIT_DIR.mkdirs();
        Path.BRANCH_DIR.mkdirs();
        Path.BLOB_DIR.mkdirs();

        // The initial commit has no files, no parent, and the time 00:00:00 UTC, 1 January 1970.
        Commit initialCommit = new Commit("initial commit", new Date(0), null, null, new TreeMap<>());
        String id = initialCommit.saveCommit();

        Branch.writeBranch("master", id);
        Branch.writeHead("master");
        new StagingArea().save();
    }

    /** Returns true if the current directory has a Gitlet repository. */
    public static boolean isInitialized() {
        return Path.GITLET_DIR.isDirectory();
    }
}
