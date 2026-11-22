package gitlet.repository;

import gitlet.Utils;

import java.io.File;
import java.util.List;

/**
 * Reads and writes branches and HEAD.
 * Each branch is a file in .gitlet/branches that holds the id of its head commit,
 * and the HEAD file holds the name of the current branch.
 */
public class Branch {

    /** Returns the id of the head commit of this branch. */
    public static String readBranch(String branchName) {
        return Utils.readContentsAsString(branchFile(branchName));
    }

    /** Makes this branch point to this commit. Creates the branch if it does not exist. */
    public static void writeBranch(String branchName, String commitId) {
        Utils.writeContents(branchFile(branchName), commitId);
    }

    /** Returns true if a branch with this name exists. */
    public static boolean exists(String branchName) {
        return branchFile(branchName).isFile();
    }

    /** Deletes the branch pointer only. Its commits are kept. */
    public static void delete(String branchName) {
        branchFile(branchName).delete();
    }

    /** Returns the names of all branches, sorted. */
    public static List<String> listBranches() {
        return Utils.plainFilenamesIn(Path.BRANCH_DIR);
    }

    /** Returns the name of the current branch. */
    public static String readHead() {
        return Utils.readContentsAsString(Path.HEAD_FILE);
    }

    /** Makes this branch the current branch. */
    public static void writeHead(String branchName) {
        Utils.writeContents(Path.HEAD_FILE, branchName);
    }

    /** Returns the id of the current commit (the head commit of the current branch). */
    public static String currentCommitId() {
        return readBranch(readHead());
    }

    private static File branchFile(String branchName) {
        return Utils.join(Path.BRANCH_DIR, branchName);
    }
}
