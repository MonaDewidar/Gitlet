package gitlet.services;

import gitlet.Utils;
import gitlet.repository.Branch;
import gitlet.repository.Commit;

public class LogCommand {
    public static void  logCommand(){
//        ge the first commit
        String branch= Branch.readHead();
        String commitId=Branch.readBranch(branch);
        Commit commit=Commit.loadCommit(commitId);
        while(commit.getParent()!=null){
            System.out.println(commit.toLogString(commitId));
            commitId=commit.getParent();
            commit= Commit.loadCommit(commitId);
        }
        // initial commit
        System.out.println(commit.toLogString(commitId));

    }
}
