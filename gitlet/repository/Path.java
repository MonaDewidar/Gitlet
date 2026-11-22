package gitlet.repository;

import gitlet.Utils;

import java.io.File;

/** Locations of all the files and directories that Gitlet uses. */
public class Path {

    /** The current working directory, which holds the user's files. */
    public static final File CWD = new File(System.getProperty("user.dir"));

    /** The .gitlet directory, which holds the whole repository. */
    public static final File GITLET_DIR = Utils.join(CWD, ".gitlet");

    /** One file per commit, named by the commit id. */
    public static final File COMMIT_DIR = Utils.join(GITLET_DIR, "commits");

    /** One file per version of a file, named by the blob id. */
    public static final File BLOB_DIR = Utils.join(GITLET_DIR, "blobs");

    /** One file per branch, holding the id of the branch's head commit. */
    public static final File BRANCH_DIR = Utils.join(GITLET_DIR, "branches");

    /** The saved staging area. */
    public static final File STAGING_FILE = Utils.join(GITLET_DIR, "staging");

    /** Holds the name of the current branch. */
    public static final File HEAD_FILE = Utils.join(GITLET_DIR, "head");
}
