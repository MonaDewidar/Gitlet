package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.TreeMap;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    // this means where is the user right now
            // current working directory
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    // where will gitlet keep its data
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    // where the commits are saved
    public static final File COMMITS_DIR=join(GITLET_DIR,"commits");
    //blobs
   public static final File  BLOBS_DIR = join(GITLET_DIR, "blobs");
    //head_dir
    public static final File HEADS_DIR = join(GITLET_DIR, "refs", "heads");
    // head_file
    public static final File HEAD_FILE = join(GITLET_DIR, "HEAD");

    /* TODO: fill in the rest of this class. */



    // for init function -> create new repo in this folder
    public static void init(){
        if(COMMITS_DIR.exists()){
            throw new GitletException(
                    "A Gitlet version-control system already exists in the current directory.");
        }
        // create the commit, blobs,head
        COMMITS_DIR.mkdirs();
        BLOBS_DIR.mkdirs();
        HEADS_DIR.mkdirs();

        // initial commit location of the object in the memory
        Commit initial=new Commit("Initial commit",new Date(0),null,null,new TreeMap<>());

        // get id
        // seri-> convert in into bytes , then hashing
        String id=sha1(serialize(initial));
        // saving now


        // join(COMMITS_DIR,id)-> build an address …\.gitlet\commits\3f2a9c51e0…
        // WO in Utils -> create a file and put the comment in the file (address above)
        writeObject(join(COMMITS_DIR,id), initial);

        // master branch point to the initial
        writeContents(join(HEADS_DIR,"master"),id);

        // master is the current

        writeContents(HEAD_FILE,"master");


    }

}
