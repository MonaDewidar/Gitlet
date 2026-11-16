package gitlet;
import gitlet.services.InitCommand;

public class Main {

    public static void main(String[] args) {

        try {
            if(args.length==0){
                throw new GitletException("Please enter a command.");
            }
            String command=args[0];
            switch (command) {
                case "init":
                    if (args.length > 1) {
                        throw new GitletException("Incorrect operands.");
                    }
                    InitCommand.init();
                    break;


                default:
                    throw new GitletException("No command with that name exists.");
            }
        }catch (GitletException e){
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }
}
