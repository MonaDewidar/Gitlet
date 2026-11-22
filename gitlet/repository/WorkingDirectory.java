package gitlet.repository;

import gitlet.GitletException;
import gitlet.Utils;

import java.io.File;
import java.util.List;

/** Reads, writes, deletes and lists the user's files in the current working directory. */
public class WorkingDirectory {

    /** Returns the contents of this file. */
    public static byte[] readFile(String fileName) {
        File file = Utils.join(Path.CWD, fileName);
        if (!file.isFile()) {
            throw new GitletException("File does not exist.");
        }
        return Utils.readContents(file);
    }

    public static void writeFile(String fileName, byte[] content) {
        Utils.writeContents(Utils.join(Path.CWD, fileName), (Object) content);
    }

    public static void writeFile(String fileName, String content) {
        Utils.writeContents(Utils.join(Path.CWD, fileName), content);
    }

    /** Writes the version of this file that is saved in this blob. */
    public static void restoreFile(String fileName, String blobId) {
        writeFile(fileName, Blob.readBytes(blobId));
    }

    public static void deleteFile(String fileName) {
        Utils.restrictedDelete(Utils.join(Path.CWD, fileName));
    }

    /** Returns the blob id of this file's current contents, or null if the file does not exist. */
    public static String blobIdOf(String fileName) {
        File file = Utils.join(Path.CWD, fileName);
        if (!file.isFile()) {
            return null;
        }
        return Blob.idOf(Utils.readContents(file));
    }

    /** Returns the names of all plain files in the working directory, sorted. */
    public static List<String> listFiles() {
        return Utils.plainFilenamesIn(Path.CWD);
    }
}
