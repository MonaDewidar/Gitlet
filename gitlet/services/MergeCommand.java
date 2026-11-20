package gitlet.services;

import gitlet.GitletException;
import gitlet.Utils;
import gitlet.repository.*;

import java.util.*;

public class MergeCommand {
    public static void mergeCommand(String branchName){
        String currentBranchName = Branch.readHead();

        if(currentBranchName.equals(branchName)){
            throw new GitletException("Cannot merge a branch with itself.");
        }
        // DELETED: the unused "commits" list
        if(!(Utils.join(Path.Branch_DIR,branchName).exists())){
            throw new GitletException("A branch with that name does not exist.");
        }
        StagingArea stagingArea=StagingArea.loadStagingContent();
        if(!(stagingArea.getStagingFiles().isEmpty() && stagingArea.getStagingRemove().isEmpty())){
            throw new GitletException("You have uncommitted changes.");
        }

        String commitId= Branch.readBranch(branchName);
        String givenBranchCommitId=commitId;
        List<String>targetBranchCommits=new ArrayList<>();
        while(commitId!=null){
            targetBranchCommits.add(commitId);
            commitId=Commit.loadCommit(commitId).getParent();
        }

        String splitPointCommitID=null;
        String currentCommitId=Branch.readBranch(currentBranchName);
        String currentCommitIdTemp=currentCommitId;
        while(currentCommitId!=null){
            if(targetBranchCommits.contains(currentCommitId)){
                splitPointCommitID=currentCommitId;
                break;
            }
            currentCommitId=Commit.loadCommit(currentCommitId).getParent();
        }

        if(splitPointCommitID.equals(givenBranchCommitId)){
            throw new GitletException("Given branch is an ancestor of the current branch.");
        }
        if(splitPointCommitID.equals(currentCommitIdTemp)){
            CheckoutCommand.checkoutBranch(branchName);
            throw new GitletException("Current branch fast-forwarded.");
        }

        TreeMap<String,String> filesInGivenBranch=Commit.loadCommit(givenBranchCommitId).getBlobs();
        TreeMap<String,String> filesInCurrentBranch=Commit.loadCommit(currentCommitIdTemp).getBlobs();

        List<String> WDFiles = WorkingDirectory.loadFilesInWD();
        for(String file: filesInGivenBranch.keySet()){
            if(WDFiles.contains(file) && (!(filesInCurrentBranch.containsKey(file)))){
                throw new GitletException("There is an untracked file in the way; delete it, or add and commit it first.");
            }
        }

        TreeMap<String,String>filesInSplitPoint=Commit.loadCommit(splitPointCommitID).getBlobs();
        Set<String>all=new TreeSet<>();
        all.addAll(filesInSplitPoint.keySet());
        all.addAll(filesInCurrentBranch.keySet());
        all.addAll(filesInGivenBranch.keySet());

        boolean conflict=false;
        for(String file:all){
            String splitID=filesInSplitPoint.get(file);
            String currentID=filesInCurrentBranch.get(file);
            String givenID=filesInGivenBranch.get(file);

            boolean currentModified=!Objects.equals(currentID,splitID);
            boolean givenModified=!Objects.equals(givenID,splitID);

            // modified in the given branch only -> take the given version
            if(givenModified && !currentModified){
                if(givenID!=null) {
                    CheckoutCommand.checkoutFileWithSpecificCommitId(givenBranchCommitId, file);
                    stagingArea.addingFiles(file,givenID);
                }
                else {
                    // the given branch deleted it
                    WorkingDirectory.deleteFilesFromWD(file);
                    stagingArea.removingFiles(file);
                }
            }
            // modified in both branches, in different ways -> conflict
            if(currentModified && givenModified && !Objects.equals(givenID,currentID)){
                String currentContent=(currentID==null)?"":Blob.readContent(currentID);
                String givenContent=(givenID==null)?"":Blob.readContent(givenID);

                String content = "<<<<<<< HEAD\n" + currentContent + "=======\n" + givenContent + ">>>>>>>\n";
                WorkingDirectory.writeFilesIntoWD(file,content);
                stagingArea.addingFiles(file,Blob.saveContent(content));
                conflict=true;
            }
        }
        stagingArea.saveStagingContent();

        CommitCommand.addCommit("Merged " + branchName + " into " + currentBranchName + ".", givenBranchCommitId);   // NEW: the merge commit

        if(conflict){
            System.out.println("Encountered a merge conflict.");
        }
    }
}