package gitlet.services;

import gitlet.GitletException;
import gitlet.Utils;
import gitlet.repository.*;

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
        String branch= Branch.readHead();
        String commitId=Branch.readBranch(branch);
        Commit commit=Commit.loadCommit(commitId);
        TreeMap<String,String>blobs=commit.getBlobs();
        TreeMap<String,String>stagedFiles=stagingArea.getStagingFiles();

        // TreeMap -> the files are printed in sorted order
        TreeMap<String,String>modifications=new TreeMap<>();

        //Tracked in the current commit, changed in the working directory, but not staged;
        //or not staged for removal, but tracked in the current commit and deleted from the working directory
        for(Map.Entry<String,String> entry:blobs.entrySet()){
            String fileName=entry.getKey();
            if(stagedFiles.containsKey(fileName) || stagedRemoved.contains(fileName)){
                continue;
            }
            String wdId=WorkingDirectory.blobIdOf(fileName);
            if(wdId==null){
                modifications.put(fileName,"deleted");
            }
            else if(!(wdId.equals(entry.getValue()))){
                modifications.put(fileName,"modified");
            }
        }

        //Staged for addition, but with different contents than in the working directory;
        //or staged for addition, but deleted in the working directory
        for(Map.Entry<String,String> entry:stagedFiles.entrySet()){
            String fileName=entry.getKey();
            String wdId=WorkingDirectory.blobIdOf(fileName);
            if(wdId==null){
                modifications.put(fileName,"deleted");
            }
            else if(!(wdId.equals(entry.getValue()))){
                modifications.put(fileName,"modified");
            }
        }

        for(Map.Entry<String,String> entry:modifications.entrySet()){
            System.out.println(entry.getKey()+" ("+entry.getValue()+")");
        }
        System.out.println();


        System.out.println("=== Untracked Files ===");
        // not staged for addition and not tracked,
        // this includes files that were staged for removal and then created again
        List<String>files=WorkingDirectory.loadFilesInWD();
        for(String file:files){
            boolean staged=stagedFiles.containsKey(file);
            boolean tracked=blobs.containsKey(file) && !stagedRemoved.contains(file);
            if(!staged && !tracked) {
                System.out.println(file);
            }
        }
        System.out.println();

    }
}
