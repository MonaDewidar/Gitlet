package gitlet.repository;

import gitlet.GitletException;
import gitlet.Utils;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.sha1;

public class Commit implements Serializable {

    private static final long serialVersionUID = 1L;
    // length of a full sha1 id
    private static final int ID_LENGTH = 40;

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

    public String getMessage() {
        return message;
    }

    public Date getTimeStamp() {
        return TimeStamp;
    }

    public String getParent() {
        return parent;
    }

    public String getSecParent() {
        return secParent;
    }

    public TreeMap<String, String> getBlobs() {
        return blobs;
    }


    // save
    // write a file in .gitlet/commit
    public String saveCommit(){
        // first get the id of the commit
        String id=sha1((Object) Utils.serialize(this));
        // build file
        File file=Utils.join(Path.COMMIT_DIR,id);
        // write the commit in this file
        Utils.writeObject(file,this);
        return id;

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

    // the text for one commit in the log
    public  String toLogString(String id) {
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.ENGLISH);
        String result = "===\n";
        result += "commit " + id + "\n";
        if (secParent != null) {
            // only for merge commits: first 7 characters of each parent
            result += "Merge: " + parent.substring(0, 7) + " " + secParent.substring(0, 7) + "\n";
        }
        result += "Date: " + format.format(TimeStamp) + "\n";
        result += message + "\n";
        return result;
    }


    // load
    public static Commit loadCommit(String id){
        // get the commitFile with this id
        if(id==null || id.isEmpty()){
            throw new GitletException("No commit with that id exists.");
        }
        File file=Utils.join(Path.COMMIT_DIR,id);
        if(!(file.isFile())) {
            throw new GitletException("No commit with that id exists.");
        }
        return Utils.readObject(file,Commit.class);
    }

    // full id from a full or abbreviated id (less than 40 characters)
    public static String findFullId(String idAp){
        if(idAp==null || idAp.isEmpty() || idAp.length()>ID_LENGTH){
            throw new GitletException("No commit with that id exists.");
        }
        if(idAp.length()==ID_LENGTH){
            if(Utils.join(Path.COMMIT_DIR,idAp).isFile()){
                return idAp;
            }
            throw new GitletException("No commit with that id exists.");
        }
        for(String id:listCommit()){
            if(id.startsWith(idAp)){
                return id;
            }
        }
        throw new GitletException("No commit with that id exists.");
    }


    // list

    public static List<String> listCommit(){
        List<String> all=Utils.plainFilenamesIn(Path.COMMIT_DIR);
        if(all==null){
            return new ArrayList<>();
        }
        return all;
    }
}
