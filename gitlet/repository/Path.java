package gitlet.repository;

import gitlet.Utils;

import java.io.File;

public class Path {

    public static final File CWD=new File (System.getProperty("user.dir"));
    public static final File GITLET_DIR= Utils.join(CWD,".gitlet");
    public static final File COMMIT_DIR=Utils.join(GITLET_DIR,"commits");
    public static final File BLOB_DIR=Utils.join(GITLET_DIR,"blobs");
    public static final File branch_DIR=Utils.join(GITLET_DIR,"branches");
    public static final File STAGING_FILE = Utils.join(GITLET_DIR, "staging");
    public static final File HEAD_FILE = Utils.join(GITLET_DIR, "head");
}
