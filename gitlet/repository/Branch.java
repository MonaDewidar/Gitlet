package gitlet.repository;

import gitlet.Utils;

import java.io.File;
import java.io.Serializable;

public class Branch implements Serializable {
    //     read / write a branch pointer


    // return the commit_id of the current branch
    public String readBranch(String name){
        File file= Utils.join(Path.BLOB_DIR,name);
        return Utils.readContentsAsString(file);
    }

    public void writeBranch(String commitId){
        Utils.writeContents(Path.branch_DIR,commitId);
    }




    //     read / write   HEAD, get the current commit

    // return the current branch
    public String readHead(){
        return Utils.readContentsAsString(Path.HEAD_FILE);
    }

    public void writeHead(String name){
        Utils.writeContents(Path.HEAD_FILE,name);
    }

}
