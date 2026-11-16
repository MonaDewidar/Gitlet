package gitlet.repository;

import gitlet.Utils;

import java.io.File;
import java.io.Serializable;

public class Branch  {
    //     read / write a branch pointer


    // return the commit_id of the current branch
    public static String readBranch(String name){
        File file= Utils.join(Path.Branch_DIR,name);
        return Utils.readContentsAsString(file);
    }

    public static void writeBranch(String branchName, String commitId){
        File file=Utils.join(Path.Branch_DIR,branchName);
        Utils.writeContents(file,commitId);
    }




    //     read / write   HEAD, get the current commit

    // return the current branch
    public static String readHead(){
        return Utils.readContentsAsString(Path.HEAD_FILE);
    }

    public static void writeHead(String name){
        Utils.writeContents(Path.HEAD_FILE,name);
    }

}
