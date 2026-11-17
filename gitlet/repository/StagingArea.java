package gitlet.repository;

import gitlet.GitletException;
import gitlet.Utils;

import java.io.File;
import java.io.Serializable;
import java.util.TreeMap;
import java.util.TreeSet;

public class StagingArea implements Serializable {
    // add
    // if there is no change ?
    // alphabetic sort
    TreeMap<String,String>stagingFiles=new TreeMap<>();
    public   void addingFiles(String fileName, String blobId){
        // staging it
        stagingRemove.remove(fileName);
        stagingFiles.put(fileName, blobId);
    }
    // remove -> files to drop from the next commit
    // unstage the file
     TreeSet<String>stagingRemove=new TreeSet<>();

    public void removingFiles(String fileName){
            stagingRemove.add(fileName);
//            stagingFiles.remove(fileName); in case add a.txt then rm a.txt it does not commited yet
        }


    public TreeMap<String, String> getStagingFiles() {
        return stagingFiles;
    }

    public void setStagingFiles(TreeMap<String, String> stagingFiles) {
        this.stagingFiles = stagingFiles;
    }

    public TreeSet<String> getStagingRemove() {
        return stagingRemove;
    }

    public void setStagingRemove(TreeSet<String> stagingRemove) {
        this.stagingRemove = stagingRemove;
    }

    public void unstage(String fileName){
        stagingRemove.remove(fileName);
        stagingFiles.remove(fileName);
    }



    // save
    public  void saveStagingContent(){
        Utils.writeObject(Path.STAGING_FILE,this);
    }

    // load
    public static StagingArea loadStagingContent(){
       if(Path.STAGING_FILE.exists()){
           return Utils.readObject(Path.STAGING_FILE,StagingArea .class);
       }
       else {
           return new StagingArea();
       }
    }
    // after the end of every command
    public void clear(){
        stagingFiles.clear();
        stagingRemove.clear();
    }





}