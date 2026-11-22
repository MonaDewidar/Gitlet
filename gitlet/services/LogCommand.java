package gitlet.services;

import gitlet.GitletException;
import gitlet.repository.Branch;
import gitlet.repository.Commit;

/** log, global-log and find. */
public class LogCommand {

    /** log: prints the current commit and its first parents back to the initial commit. */
    public static void log() {
        String commitId = Branch.currentCommitId();
        while (commitId != null) {
            Commit commit = Commit.loadCommit(commitId);
            System.out.println(commit.toLogString(commitId));
            // Second parents of merge commits are not followed.
            commitId = commit.getParent();
        }
    }

    /** global-log: prints every commit ever made, in any order. */
    public static void globalLog() {
        for (String id : Commit.listCommitIds()) {
            System.out.println(Commit.loadCommit(id).toLogString(id));
        }
    }

    /** find [message]: prints the ids of all commits with this message. */
    public static void find(String message) {
        boolean found = false;
        for (String id : Commit.listCommitIds()) {
            if (Commit.loadCommit(id).getMessage().equals(message)) {
                System.out.println(id);
                found = true;
            }
        }
        if (!found) {
            throw new GitletException("Found no commit with that message.");
        }
    }
}
