package gitlet;

// TODO: any imports you need here

import java.io.Serializable;
import java.util.Date;
import java.util.TreeMap;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private final String message;
    private final Date timestamp;
    private final String parent;
    private final String secondParent;
    // not hashmap-> cause we need the sorted style alpha
    private final TreeMap<String,String> blobs;



    /* TODO: fill in the rest of this class. */
     Commit(String message,Date timestamp,String parent,String secondParent,TreeMap<String,String> blobs) {
        this.message = message;
        this.timestamp=timestamp;
        this.parent=parent;
        this.secondParent=secondParent;
        this.blobs=blobs;
    }

   }
