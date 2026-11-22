package gitlet.services;

import gitlet.GitletException;
import gitlet.repository.Blob;
import gitlet.repository.Branch;
import gitlet.repository.Commit;
import gitlet.repository.StagingArea;
import gitlet.repository.WorkingDirectory;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/** merge [branch name]: merges the given branch into the current branch. */
public class MergeCommand {

    public static void merge(String branchName) {
        String currentBranch = Branch.readHead();
        StagingArea stagingArea = StagingArea.load();

        if (!stagingArea.isEmpty()) {
            throw new GitletException("You have uncommitted changes.");
        }
        if (!Branch.exists(branchName)) {
            throw new GitletException("A branch with that name does not exist.");
        }
        if (branchName.equals(currentBranch)) {
            throw new GitletException("Cannot merge a branch with itself.");
        }

        String currentCommitId = Branch.currentCommitId();
        String givenCommitId = Branch.readBranch(branchName);
        String splitPointId = findSplitPoint(currentCommitId, givenCommitId);

        if (splitPointId.equals(givenCommitId)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }
        if (splitPointId.equals(currentCommitId)) {
            CheckoutCommand.checkoutBranch(branchName);
            System.out.println("Current branch fast-forwarded.");
            return;
        }

        TreeMap<String, String> splitFiles = Commit.loadCommit(splitPointId).getBlobs();
        TreeMap<String, String> currentFiles = Commit.loadCommit(currentCommitId).getBlobs();
        TreeMap<String, String> givenFiles = Commit.loadCommit(givenCommitId).getBlobs();

        Set<String> allFiles = new TreeSet<>();
        allFiles.addAll(splitFiles.keySet());
        allFiles.addAll(currentFiles.keySet());
        allFiles.addAll(givenFiles.keySet());

        // 1. Decide what happens to every file, without changing anything yet.
        TreeMap<String, String> toCheckout = new TreeMap<>();  // file name -> blob id in the given branch
        TreeSet<String> toRemove = new TreeSet<>();
        TreeSet<String> conflicts = new TreeSet<>();
        for (String fileName : allFiles) {
            String splitId = splitFiles.get(fileName);
            String currentId = currentFiles.get(fileName);
            String givenId = givenFiles.get(fileName);

            boolean changedInCurrent = !Objects.equals(currentId, splitId);
            boolean changedInGiven = !Objects.equals(givenId, splitId);

            if (changedInGiven && !changedInCurrent) {
                // Changed only in the given branch: take the given version (or its deletion).
                if (givenId == null) {
                    toRemove.add(fileName);
                } else {
                    toCheckout.put(fileName, givenId);
                }
            } else if (changedInCurrent && changedInGiven && !Objects.equals(currentId, givenId)) {
                // Changed in both branches in different ways.
                conflicts.add(fileName);
            }
            // Otherwise it changed only in the current branch, or the same way in both: keep it.
        }

        // 2. Stop if an untracked file would be overwritten or deleted.
        for (String fileName : WorkingDirectory.listFiles()) {
            boolean untracked = !currentFiles.containsKey(fileName);
            boolean changedByMerge = toCheckout.containsKey(fileName)
                    || toRemove.contains(fileName) || conflicts.contains(fileName);
            if (untracked && changedByMerge) {
                throw new GitletException(CheckoutCommand.UNTRACKED_FILE_ERROR);
            }
        }

        // 3. Apply the changes and stage them.
        for (Map.Entry<String, String> entry : toCheckout.entrySet()) {
            WorkingDirectory.restoreFile(entry.getKey(), entry.getValue());
            stagingArea.stageForAddition(entry.getKey(), entry.getValue());
        }
        for (String fileName : toRemove) {
            WorkingDirectory.deleteFile(fileName);
            stagingArea.stageForRemoval(fileName);
        }
        for (String fileName : conflicts) {
            String content = conflictContent(currentFiles.get(fileName), givenFiles.get(fileName));
            WorkingDirectory.writeFile(fileName, content);
            stagingArea.stageForAddition(fileName, Blob.save(content));
        }
        stagingArea.save();

        // 4. The merge commit has two parents: the current head and the given branch's head.
        CommitCommand.commit("Merged " + branchName + " into " + currentBranch + ".", givenCommitId);

        if (!conflicts.isEmpty()) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    /** The contents of a conflicted file. A deleted file counts as an empty file. */
    private static String conflictContent(String currentBlobId, String givenBlobId) {
        String currentContent = (currentBlobId == null) ? "" : Blob.readContent(currentBlobId);
        String givenContent = (givenBlobId == null) ? "" : Blob.readContent(givenBlobId);
        return "<<<<<<< HEAD\n" + currentContent + "=======\n" + givenContent + ">>>>>>>\n";
    }

    /**
     * Returns the split point: the latest common ancestor of the two commits.
     * Both parents of merge commits are followed.
     */
    static String findSplitPoint(String currentCommitId, String givenCommitId) {
        Set<String> givenAncestors = ancestorsOf(givenCommitId);

        // BFS from the current commit: the first common ancestor found is the closest one.
        Set<String> visited = new HashSet<>();
        Deque<String> queue = new ArrayDeque<>();
        queue.add(currentCommitId);
        while (!queue.isEmpty()) {
            String id = queue.poll();
            if (!visited.add(id)) {
                continue;
            }
            if (givenAncestors.contains(id)) {
                return id;
            }
            addParents(queue, id);
        }
        throw new GitletException("No split point found.");
    }

    /** Returns this commit and all its ancestors (BFS over both parents). */
    private static Set<String> ancestorsOf(String commitId) {
        Set<String> ancestors = new HashSet<>();
        Deque<String> queue = new ArrayDeque<>();
        queue.add(commitId);
        while (!queue.isEmpty()) {
            String id = queue.poll();
            if (ancestors.add(id)) {
                addParents(queue, id);
            }
        }
        return ancestors;
    }

    private static void addParents(Deque<String> queue, String commitId) {
        Commit commit = Commit.loadCommit(commitId);
        if (commit.getParent() != null) {
            queue.add(commit.getParent());
        }
        if (commit.getSecondParent() != null) {
            queue.add(commit.getSecondParent());
        }
    }
}
