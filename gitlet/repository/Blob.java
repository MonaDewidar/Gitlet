package gitlet.repository;

import gitlet.GitletException;
import gitlet.Utils;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * Saves file contents in .gitlet/blobs.
 * The id of a blob is the SHA-1 hash of its contents, so the same contents are saved only once.
 */
public class Blob {

    /** Returns the blob id of this content. */
    public static String idOf(byte[] content) {
        return Utils.sha1((Object) content);
    }

    /** Saves this content (if it is not saved already) and returns its id. */
    public static String save(byte[] content) {
        String id = idOf(content);
        File file = Utils.join(Path.BLOB_DIR, id);
        if (!file.exists()) {
            Utils.writeContents(file, (Object) content);
        }
        return id;
    }

    /** Saves this text and returns its id. */
    public static String save(String content) {
        return save(content.getBytes(StandardCharsets.UTF_8));
    }

    /** Returns the contents of the blob with this id. */
    public static byte[] readBytes(String id) {
        File file = Utils.join(Path.BLOB_DIR, id);
        if (!file.exists()) {
            throw new GitletException("There isn't any blob with this id.");
        }
        return Utils.readContents(file);
    }

    /** Returns the contents of the blob with this id as text. */
    public static String readContent(String id) {
        return new String(readBytes(id), StandardCharsets.UTF_8);
    }
}
