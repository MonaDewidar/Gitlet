package gitlet.repository;

import gitlet.GitletException;
import gitlet.Utils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import static gitlet.Utils.sha1;
import static java.lang.System.exit;

public class Commit implements Serializable {


    private final String message;
    private final Date TimeStamp;
    private final String parent;
    private final String secParent;
    private final TreeMap<String, String> blobs;

    public Commit(String message, Date TimeStamp, String parent, String secParent, TreeMap<String, String> blobs) {
        this.message = message;
        this.TimeStamp = TimeStamp;
        this.parent = parent;
        this.secParent = secParent;
        this.blobs = blobs;
    }

    // save
    // write a file in .gitlet/commit
    public void saveCommit(){
        // first get the id of the commit
        String id=sha1((Object) Utils.serialize(this));
        // build file
        File file=Utils.join(Path.COMMIT_DIR,id);
        // write the commit in this file
        Utils.writeContents(file,this);

    }

    @Override
    public String toString() {
        return "Commit{" +
                "message='" + message + '\'' +
                ", TimeStamp=" + TimeStamp +
                ", parent='" + parent + '\'' +
                ", secparent='" + secParent + '\'' +
                ", blobs=" + blobs +
                '}';
    }

    // load
    public Commit loadCommit(String id){
        // get the commitFile with this id
        File file=Utils.join(Path.COMMIT_DIR,id);
        if(!(file.exists())) {
            throw new GitletException("There isn't any commit with this id");
        }
        return Utils.readObject(file,Commit.class);
    }


    // list

    public List<String> listCommit(){
        List<String> all=new ArrayList<>();
        all=Utils.plainFilenamesIn(Path.COMMIT_DIR);
        return all;
    }
}
