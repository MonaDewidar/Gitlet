package gitlet;
import org.junit.Test;

import static gitlet.Repository.init;
import static java.lang.System.exit;

public class Main {

    public static void main(String[] args) {
        if(args.length==0){
            throw new GitletException("Please enter a command");
        }
        String str=args[0];
        try {
            switch (str) {

                case "init":
                    if(args.length==1) {
                        init();
                    }
                    else {
                        System.out.println("No command with that name exists.");
                        exit(0);
                    }
                    break;
                default:
                    throw new GitletException("No command with that name exists.");
            }
        }catch(GitletException e){
            System.out.println(e.getMessage());
            exit(0);
        }







    }
}
