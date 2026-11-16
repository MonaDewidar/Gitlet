package gitlet.services;
import gitlet.GitletException;
import gitlet.repository.Branch;
import gitlet.repository.Commit;
import gitlet.repository.Path;
import java.util.Date;
import java.util.TreeMap;

public class InitCommand  {
public static void  init() {
    if(Path.GITLET_DIR.exists()){
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

}
}
