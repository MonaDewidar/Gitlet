package gitlet.repository;

import gitlet.GitletException;
import gitlet.Utils;

import java.io.File;
import java.io.Serializable;
import java.util.TreeMap;
import java.util.TreeSet;

public class StagingArea implements Serializable {
    private static final long serialVersionUID = 1L;
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
            // a file can not be staged for addition and removal at the same time
            stagingFiles.remove(fileName);
            stagingRemove.add(fileName);
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
       if(Path.STAGING_FILE.isFile()){
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