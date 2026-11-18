package gitlet.services;

import gitlet.GitletException;
import gitlet.Utils;
import gitlet.repository.*;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class LogCommand {

    public static void  logCommand(){
//        get the first commit
        String branch= Branch.readHead();
        String commitId=Branch.readBranch(branch);
        Commit commit=Commit.loadCommit(commitId);
        while(commit.getParent()!=null){
            System.out.println(commit.toLogString(commitId));
            commitId=commit.getParent();
            commit= Commit.loadCommit(commitId);
        }
        // initial commit
        System.out.println(commit.toLogString(commitId));

    }


    public static void globalLogCommand(){
        List<String>allCommitId=Commit.listCommit();
        for(String id:allCommitId){
            Commit commit=Commit.loadCommit(id);
            System.out.println(commit.toLogString(id));
        }
    }


    public static void findCommand(String message){
        List<String>allCommitId=Commit.listCommit();
        boolean find=false;
        for(String id:allCommitId){
            Commit commit=Commit.loadCommit(id);
            if(commit.getMessage().equals(message)){
                find=true;
                System.out.println(id);
            }
        }
        if(!find)
            throw new GitletException("Found no commit with that message.");
    }


    public static void statusCommand(){

        StagingArea stagingArea=StagingArea.loadStagingContent();


        // branches-> after branch command
        System.out.println("=== Branches ===");
        String head=Branch.readHead();
        List<String>branches=Utils.plainFilenamesIn(Path.Branch_DIR);
        for(String name:branches){
            if(name.equals(head)){
                System.out.print("*");
            }
            System.out.println(name);
        }

        System.out.println();
        System.out.println("=== Staged Files ===");
        TreeMap<String,String> stagedFilesNames= stagingArea.getStagingFiles();
        // keySet -> set<string>names
        for(String name:stagedFilesNames.keySet()){
            System.out.println(name);
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
        TreeSet<String> stagedRemoved=stagingArea.getStagingRemove();
        for(String name:stagedRemoved){
            System.out.println(name);
        }
        System.out.println();

        System.out.println("=== Modifications Not Staged For Commit ===");
        //Tracked in the current commit, changed in the working directory, but not staged;
        String branch= Branch.readHead();
        String commitId=Branch.readBranch(branch);
        Commit commit=Commit.loadCommit(commitId);


        TreeMap<String,String>blobs=commit.getBlobs();

        for(Map.Entry<String,String> entry:blobs.entrySet()){
            String blobName=entry.getKey();
            String blobId=entry.getValue();
            File file=Utils.join(Path.CWD,blobName);
            // staged already
            if(stagingArea.getStagingFiles().containsKey(blobName)){
                continue;
            }

            if(!file.exists()){
                if(!(stagingArea.getStagingRemove().contains(blobName))) {
                    System.out.println(blobName + " (deleted)");

                }
                continue;
            }
            String id=Utils.sha1((Object) Utils.serialize(Utils.readContentsAsString(Utils.join(Path.CWD,blobName))));
            if(!(id.equals(blobId))){
                System.out.println(blobName +" (modified)");
            }


        }
        System.out.println();


        System.out.println("=== Untracked Files ===");
        List<String>files=WorkingDirectory.loadFilesInWD();
        for(String file:files){
            boolean staged=stagingArea.getStagingFiles().containsKey(file);
            boolean tracked=commit.getBlobs().containsKey(file);
            if(!staged && !tracked) {
                System.out.println(file);
            }
        }
        System.out.println();

    }
}
