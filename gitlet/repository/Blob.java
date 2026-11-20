package gitlet.repository;

import gitlet.GitletException;
import gitlet.Utils;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class Blob {
//save content and get its id, read content by id

   // the id depends only on the content, so the same content always has the same id
   public static String idOf(byte[] content){
       return Utils.sha1((Object) content);
   }

   public static String saveContent(byte[] content){
       String id=idOf(content);
       File file= Utils.join(Path.BLOB_DIR,id);
       if(!file.exists()){
           Utils.writeContents(file,(Object) content);
       }
       return id;
   }

   public static String saveContent(String content){
       return saveContent(content.getBytes(StandardCharsets.UTF_8));
   }

   public static byte[] readBytes(String id){
       File file=Utils.join(Path.BLOB_DIR,id);
       if(!(file.exists())){
           throw new GitletException("There isn't any blob with this id");
       }
       return Utils.readContents(file);
   }

   public static String readContent(String id){
       return new String(readBytes(id),StandardCharsets.UTF_8);
   }
}
