package gitlet.services;
import gitlet.GitletException;
import gitlet.repository.Branch;
import gitlet.repository.Commit;
import gitlet.repository.Path;
import gitlet.repository.StagingArea;

import java.util.Date;
import java.util.TreeMap;

public class InitCommand  {
public static void  init() {
    if(isInitialized()){
        throw new GitletException("A Gitlet version-control system already exists in the current directory.");
    }
    Path.COMMIT_DIR.mkdirs();
    Path.Branch_DIR.mkdirs();
    Path.BLOB_DIR.mkdirs();
    // initial commit
    Commit commit = new Commit("initial commit", new Date(0), null, null, new TreeMap<>());
    String id=commit.saveCommit();

    // no blobs

    // branch -> master
    Branch.writeBranch("master",id);
    // head -> master
    Branch.writeHead("master");
    // empty staging area
    new StagingArea().saveStagingContent();

}
public static boolean isInitialized(){
    return Path.GITLET_DIR.isDirectory();
}
}
