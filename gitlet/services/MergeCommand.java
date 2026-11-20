package gitlet.services;

import gitlet.GitletException;
import gitlet.Utils;
import gitlet.repository.*;

import java.util.*;

public class MergeCommand {
    public static void mergeCommand(String branchName){
        String currentBranchName = Branch.readHead();

        StagingArea stagingArea=StagingArea.loadStagingContent();
        if(!(stagingArea.getStagingFiles().isEmpty() && stagingArea.getStagingRemove().isEmpty())){
            throw new GitletException("You have uncommitted changes.");
        }
        if(!(Utils.join(Path.Branch_DIR,branchName).isFile())){
            throw new GitletException("A branch with that name does not exist.");
        }
        if(currentBranchName.equals(branchName)){
            throw new GitletException("Cannot merge a branch with itself.");
        }

        String givenBranchCommitId=Branch.readBranch(branchName);
        String currentCommitId=Branch.readBranch(currentBranchName);
        String splitPointCommitID=findSplitPoint(currentCommitId,givenBranchCommitId);

        if(splitPointCommitID.equals(givenBranchCommitId)){
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }
        if(splitPointCommitID.equals(currentCommitId)){
            CheckoutCommand.checkoutBranch(branchName);
            System.out.println("Current branch fast-forwarded.");
            return;
        }

        TreeMap<String,String> filesInGivenBranch=Commit.loadCommit(givenBranchCommitId).getBlobs();
        TreeMap<String,String> filesInCurrentBranch=Commit.loadCommit(currentCommitId).getBlobs();
        TreeMap<String,String> filesInSplitPoint=Commit.loadCommit(splitPointCommitID).getBlobs();

        Set<String>all=new TreeSet<>();
        all.addAll(filesInSplitPoint.keySet());
        all.addAll(filesInCurrentBranch.keySet());
        all.addAll(filesInGivenBranch.keySet());

        // first decide what happens to every file, so nothing is changed if the merge has to stop
        TreeMap<String,String> toCheckout=new TreeMap<>();   // file -> blob id of the given branch
        TreeSet<String> toRemove=new TreeSet<>();
        TreeSet<String> conflicts=new TreeSet<>();
        for(String file:all){
            String splitID=filesInSplitPoint.get(file);
            String currentID=filesInCurrentBranch.get(file);
            String givenID=filesInGivenBranch.get(file);

            boolean currentModified=!Objects.equals(currentID,splitID);
            boolean givenModified=!Objects.equals(givenID,splitID);

            // modified in the given branch only -> take the given version
            if(givenModified && !currentModified){
                if(givenID!=null) {
                    toCheckout.put(file,givenID);
                }
                else {
                    // the given branch deleted it
                    toRemove.add(file);
                }
            }
            // modified in both branches, in different ways -> conflict
            else if(currentModified && givenModified && !Objects.equals(givenID,currentID)){
                conflicts.add(file);
            }
            // modified in the current branch only, or in the same way in both -> stays as it is
        }

        // an untracked file in the current commit would be overwritten or deleted by the merge
        for(String file:WorkingDirectory.loadFilesInWD()){
            if(filesInCurrentBranch.containsKey(file)){
                continue;
            }
            if(toCheckout.containsKey(file) || toRemove.contains(file) || conflicts.contains(file)){
                throw new GitletException("There is an untracked file in the way; delete it, or add and commit it first.");
            }
        }

        for(Map.Entry<String,String> entry:toCheckout.entrySet()){
            WorkingDirectory.writeBlobIntoWD(entry.getKey(),entry.getValue());
            stagingArea.addingFiles(entry.getKey(),entry.getValue());
        }
        for(String file:toRemove){
            WorkingDirectory.deleteFilesFromWD(file);
            stagingArea.removingFiles(file);
        }
        for(String file:conflicts){
            String currentID=filesInCurrentBranch.get(file);
            String givenID=filesInGivenBranch.get(file);
            // a deleted file is treated as an empty file
            String currentContent=(currentID==null)?"":Blob.readContent(currentID);
            String givenContent=(givenID==null)?"":Blob.readContent(givenID);

            String content = "<<<<<<< HEAD\n" + currentContent + "=======\n" + givenContent + ">>>>>>>\n";
            WorkingDirectory.writeFilesIntoWD(file,content);
            stagingArea.addingFiles(file,Blob.saveContent(content));
        }
        stagingArea.saveStagingContent();

        // the merge commit has 2 parents: the current branch head and the given branch head
        CommitCommand.addCommit("Merged " + branchName + " into " + currentBranchName + ".", givenBranchCommitId);

        if(!conflicts.isEmpty()){
            System.out.println("Encountered a merge conflict.");
        }
    }

    // the latest common ancestor of the 2 commits, following both parents of merge commits
    static String findSplitPoint(String currentCommitId,String givenCommitId){
        // all the ancestors of the given branch head (including itself)
        Set<String> givenAncestors=new HashSet<>();
        Deque<String> queue=new ArrayDeque<>();
        queue.add(givenCommitId);
        while(!queue.isEmpty()){
            String id=queue.poll();
            if(!givenAncestors.add(id)){
                continue;
            }
            Commit commit=Commit.loadCommit(id);
            if(commit.getParent()!=null){
                queue.add(commit.getParent());
            }
            if(commit.getSecParent()!=null){
                queue.add(commit.getSecParent());
            }
        }

        // BFS from the current branch head, the first common commit is the closest one
        Set<String> visited=new HashSet<>();
        queue.clear();
        queue.add(currentCommitId);
        while(!queue.isEmpty()){
            String id=queue.poll();
            if(!visited.add(id)){
                continue;
            }
            if(givenAncestors.contains(id)){
                return id;
            }
            Commit commit=Commit.loadCommit(id);
            if(commit.getParent()!=null){
                queue.add(commit.getParent());
            }
            if(commit.getSecParent()!=null){
                queue.add(commit.getSecParent());
            }
        }
        throw new GitletException("No split point found.");
    }
}
