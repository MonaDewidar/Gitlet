package gitlet.repository;

import gitlet.GitletException;
import gitlet.Utils;

import java.io.File;
import java.io.Serializable;

public class Blob implements Serializable {
//save content and get its id, read content by id
   public static String saveContent(String content){
       String id=Utils.sha1((Object) Utils.serialize(content));
       File file= Utils.join(Path.BLOB_DIR,id);
       Utils.writeContents(file,content);
       return id;
   }

   public static String readContent(String id){
       File file=Utils.join(Path.BLOB_DIR,id);
       if(!(file.exists())){
           throw new GitletException("There isn't any blob with this id");
       }
       return Utils.readContentsAsString(file);
   }
}
