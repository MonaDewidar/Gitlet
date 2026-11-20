package gitlet.repository;

import gitlet.GitletException;
import gitlet.Utils;

import java.io.File;

import java.util.List;

public class WorkingDirectory  {

//    read, write, delete, list the user's files in the CWD

    // read the content of this file
    public static byte[] readFilesInWD(String fileName){
        File file= Utils.join(Path.CWD,fileName);
        if(file.isFile())
            return Utils.readContents(file);
        else
            throw new GitletException("File does not exist.");

    }

    // write
    public static void writeFilesIntoWD(String fileName, byte[] content){
        File file=Utils.join(Path.CWD,fileName);
        Utils.writeContents(file,(Object) content);
    }

    public static void writeFilesIntoWD(String fileName, String content){
        File file=Utils.join(Path.CWD,fileName);
        Utils.writeContents(file,content);
    }

    // write the version of this file that is saved in the blob
    public static void writeBlobIntoWD(String fileName, String blobId){
        writeFilesIntoWD(fileName,Blob.readBytes(blobId));
    }

    // delete
    public static void deleteFilesFromWD(String fileName){
        File file=Utils.join(Path.CWD,fileName);
        Utils.restrictedDelete(file);
    }

    // the blob id of the file in the CWD, or null if it is not there
    public static String blobIdOf(String fileName){
        File file=Utils.join(Path.CWD,fileName);
        if(!file.isFile()){
            return null;
        }
        return Blob.idOf(Utils.readContents(file));
    }


    // list -> load name of files in CWD
    public static List<String> loadFilesInWD(){
        return Utils.plainFilenamesIn(Path.CWD);
    }

}
