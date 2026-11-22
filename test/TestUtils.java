package test;

import gitlet.Utils;
import gitlet.repository.Path;

import java.io.File;

/** Helpers shared by the tests. The tests run in the current directory. */
class TestUtils {

    /** Deletes the .gitlet directory and these files, so every test starts from nothing. */
    static void cleanRepository(String... fileNames) {
        deleteFolder(Path.GITLET_DIR);
        for (String fileName : fileNames) {
            Utils.join(Path.CWD, fileName).delete();
        }
    }

    /** Deletes a folder and everything in it (File.delete only deletes empty folders). */
    static void deleteFolder(File folder) {
        File[] children = folder.listFiles();
        if (children != null) {
            for (File child : children) {
                if (child.isDirectory()) {
                    deleteFolder(child);
                } else {
                    child.delete();
                }
            }
        }
        folder.delete();
    }

    static void writeFile(String fileName, String content) {
        Utils.writeContents(Utils.join(Path.CWD, fileName), content);
    }

    static String readFile(String fileName) {
        return Utils.readContentsAsString(Utils.join(Path.CWD, fileName));
    }
}
