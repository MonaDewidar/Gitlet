package gitlet.repository;

import gitlet.Utils;

import java.io.Serializable;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * The files staged for the next commit, saved in .gitlet/staging.
 * A file is never staged for addition and for removal at the same time.
 * TreeMap and TreeSet keep the file names sorted, which status needs.
 */
public class StagingArea implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Files staged for addition: file name -> blob id. */
    private final TreeMap<String, String> stagedFiles = new TreeMap<>();

    /** Files staged for removal. */
    private final TreeSet<String> removedFiles = new TreeSet<>();

    public TreeMap<String, String> getStagedFiles() {
        return stagedFiles;
    }

    public TreeSet<String> getRemovedFiles() {
        return removedFiles;
    }

    /** Stages this version of the file for addition. */
    public void stageForAddition(String fileName, String blobId) {
        removedFiles.remove(fileName);
        stagedFiles.put(fileName, blobId);
    }

    /** Stages the file for removal. */
    public void stageForRemoval(String fileName) {
        stagedFiles.remove(fileName);
        removedFiles.add(fileName);
    }

    /** Removes the file from the staging area. */
    public void unstage(String fileName) {
        stagedFiles.remove(fileName);
        removedFiles.remove(fileName);
    }

    /** Returns true if nothing is staged. */
    public boolean isEmpty() {
        return stagedFiles.isEmpty() && removedFiles.isEmpty();
    }

    public void clear() {
        stagedFiles.clear();
        removedFiles.clear();
    }

    public void save() {
        Utils.writeObject(Path.STAGING_FILE, this);
    }

    /** Loads the saved staging area, or returns an empty one if nothing is saved. */
    public static StagingArea load() {
        if (Path.STAGING_FILE.isFile()) {
            return Utils.readObject(Path.STAGING_FILE, StagingArea.class);
        }
        return new StagingArea();
    }
}
