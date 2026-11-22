package gitlet.services;

import gitlet.repository.Branch;
import gitlet.repository.Commit;
import gitlet.repository.StagingArea;
import gitlet.repository.WorkingDirectory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** status: shows the branches, the staging area and the state of the working directory. */
public class StatusCommand {

    public static void status() {
        StagingArea stagingArea = StagingArea.load();
        Commit currentCommit = Commit.loadCommit(Branch.currentCommitId());

        printSection("Branches", branches());
        printSection("Staged Files", stagingArea.getStagedFiles().keySet());
        printSection("Removed Files", stagingArea.getRemovedFiles());
        printSection("Modifications Not Staged For Commit",
                modificationsNotStaged(stagingArea, currentCommit));
        printSection("Untracked Files", untrackedFiles(stagingArea, currentCommit));
    }

    /** Prints a section title, its lines (already sorted), and an empty line. */
    private static void printSection(String title, Collection<String> lines) {
        System.out.println("=== " + title + " ===");
        for (String line : lines) {
            System.out.println(line);
        }
        System.out.println();
    }

    /** All branch names, sorted, with a * before the current branch. */
    private static List<String> branches() {
        String currentBranch = Branch.readHead();
        List<String> lines = new ArrayList<>();
        for (String name : Branch.listBranches()) {
            lines.add(name.equals(currentBranch) ? "*" + name : name);
        }
        return lines;
    }

    /**
     * Files whose working version is different from the version that will be committed,
     * as "name (modified)" or "name (deleted)".
     */
    private static List<String> modificationsNotStaged(StagingArea stagingArea, Commit currentCommit) {
        // TreeMap: file name -> "modified" or "deleted", sorted by file name.
        TreeMap<String, String> changes = new TreeMap<>();

        // Tracked in the current commit, not staged: changed or deleted in the working directory.
        for (Map.Entry<String, String> entry : currentCommit.getBlobs().entrySet()) {
            String fileName = entry.getKey();
            if (stagingArea.getStagedFiles().containsKey(fileName)
                    || stagingArea.getRemovedFiles().contains(fileName)) {
                continue;
            }
            addIfChanged(changes, fileName, entry.getValue());
        }

        // Staged for addition: changed or deleted after it was staged.
        for (Map.Entry<String, String> entry : stagingArea.getStagedFiles().entrySet()) {
            addIfChanged(changes, entry.getKey(), entry.getValue());
        }

        List<String> lines = new ArrayList<>();
        for (Map.Entry<String, String> entry : changes.entrySet()) {
            lines.add(entry.getKey() + " (" + entry.getValue() + ")");
        }
        return lines;
    }

    /** Records the file if its working version is deleted or different from this blob. */
    private static void addIfChanged(Map<String, String> changes, String fileName, String blobId) {
        String workingBlobId = WorkingDirectory.blobIdOf(fileName);
        if (workingBlobId == null) {
            changes.put(fileName, "deleted");
        } else if (!workingBlobId.equals(blobId)) {
            changes.put(fileName, "modified");
        }
    }

    /**
     * Files in the working directory that are neither staged for addition nor tracked.
     * This includes files staged for removal and then created again.
     */
    private static List<String> untrackedFiles(StagingArea stagingArea, Commit currentCommit) {
        List<String> lines = new ArrayList<>();
        for (String fileName : WorkingDirectory.listFiles()) {
            boolean staged = stagingArea.getStagedFiles().containsKey(fileName);
            boolean tracked = currentCommit.getBlobs().containsKey(fileName)
                    && !stagingArea.getRemovedFiles().contains(fileName);
            if (!staged && !tracked) {
                lines.add(fileName);
            }
        }
        return lines;
    }
}
