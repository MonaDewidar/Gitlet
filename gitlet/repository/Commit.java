package gitlet.repository;

import gitlet.GitletException;
import gitlet.Utils;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

/**
 * A snapshot of the tracked files, saved in .gitlet/commits under its id.
 * Parents are saved as ids, not as Commit objects, so saving a commit
 * never saves the commits before it.
 */
public class Commit implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Length of a full SHA-1 id. */
    private static final int ID_LENGTH = 40;

    private final String message;
    private final Date timestamp;

    /** Id of the first parent, or null for the initial commit. */
    private final String parent;

    /** Id of the second parent for a merge commit, or null for other commits. */
    private final String secondParent;

    /** File name -> blob id of every file tracked by this commit. */
    private final TreeMap<String, String> blobs;

    public Commit(String message, Date timestamp, String parent, String secondParent,
                  TreeMap<String, String> blobs) {
        this.message = message;
        this.timestamp = timestamp;
        this.parent = parent;
        this.secondParent = secondParent;
        this.blobs = blobs;
    }

    public String getMessage() {
        return message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getParent() {
        return parent;
    }

    public String getSecondParent() {
        return secondParent;
    }

    public TreeMap<String, String> getBlobs() {
        return blobs;
    }

    /** Saves this commit and returns its id, the SHA-1 hash of the saved commit. */
    public String saveCommit() {
        String id = Utils.sha1((Object) Utils.serialize(this));
        Utils.writeObject(Utils.join(Path.COMMIT_DIR, id), this);
        return id;
    }

    /** Returns how this commit, which has this id, is printed by log and global-log. */
    public String toLogString(String id) {
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.ENGLISH);
        StringBuilder result = new StringBuilder();
        result.append("===\n");
        result.append("commit ").append(id).append("\n");
        if (secondParent != null) {
            // Only merge commits: the first 7 characters of each parent.
            result.append("Merge: ").append(parent, 0, 7)
                    .append(" ").append(secondParent, 0, 7).append("\n");
        }
        result.append("Date: ").append(format.format(timestamp)).append("\n");
        result.append(message).append("\n");
        return result.toString();
    }

    /** Loads the commit with this full id. */
    public static Commit loadCommit(String id) {
        if (id == null || id.isEmpty()) {
            throw new GitletException("No commit with that id exists.");
        }
        File file = Utils.join(Path.COMMIT_DIR, id);
        if (!file.isFile()) {
            throw new GitletException("No commit with that id exists.");
        }
        return Utils.readObject(file, Commit.class);
    }

    /** Returns the full id of a commit from its full or abbreviated id. */
    public static String findFullId(String shortId) {
        if (shortId == null || shortId.isEmpty() || shortId.length() > ID_LENGTH) {
            throw new GitletException("No commit with that id exists.");
        }
        if (shortId.length() == ID_LENGTH) {
            if (Utils.join(Path.COMMIT_DIR, shortId).isFile()) {
                return shortId;
            }
            throw new GitletException("No commit with that id exists.");
        }
        for (String id : listCommitIds()) {
            if (id.startsWith(shortId)) {
                return id;
            }
        }
        throw new GitletException("No commit with that id exists.");
    }

    /** Returns the ids of all commits ever made. */
    public static List<String> listCommitIds() {
        List<String> ids = Utils.plainFilenamesIn(Path.COMMIT_DIR);
        if (ids == null) {
            return new ArrayList<>();
        }
        return ids;
    }
}
