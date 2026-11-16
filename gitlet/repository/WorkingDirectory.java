package gitlet.repository;

import gitlet.GitletException;
import gitlet.Utils;

import java.io.File;

import java.util.List;

public class WorkingDirectory  {

//    read, write, delete, list the user's files in the CWD

    // read the content of this file
    public static String readFilesInWD(String fileName){
        File file= Utils.join(Path.CWD,fileName);
        if(file.exists())
            return Utils.readContentsAsString(file);
        else
            throw new GitletException("File does not exist.");

    }

    // write
    public static void writeFilesIntoWD(String fileName, String content){
        File file=Utils.join(Path.CWD,fileName);
        Utils.writeContents(file,content);
    }

    // delete
    public static void deleteFilesFromWD(String fileName){
        File file=Utils.join(Path.CWD,fileName);
        Utils.restrictedDelete(file);
    }


    // list -> load name of files in CWD
    public static List<String> loadFilesInWD(){
        return Utils.plainFilenamesIn(Path.CWD);
    }

}
