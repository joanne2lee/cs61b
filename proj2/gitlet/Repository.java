package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static gitlet.Utils.*;


/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Joanne Lee
 */
public class Repository {


    /** The current working directory. */
    static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    static final File GITLET_DIR = join(CWD, ".gitlet");

    /** A file tracking the staging area. */
    public static File stagingFile = join(GITLET_DIR, "stagingArea.txt");

    /** A folder containing commits, files named by commit IDs. */
    public static File commits = join(GITLET_DIR, "commits");

    /** A folder containing blobs, files named by blob IDs. */
    public static File blobs = join(GITLET_DIR, "blobs");

    /** A file tracking the branches as a HashMap, where
     * keys = the branch name, and
     * values = last commit (head) of the branch. */
    public static File branchesFile = join(GITLET_DIR, "branches.txt");

    /** A file tracking the HEAD branch. */
    public static File HEAD = join(GITLET_DIR, "HEAD.txt");





    public static void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        else {
            GITLET_DIR.mkdir();
            try {
                stagingFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            commits.mkdir();
            blobs.mkdir();
            try {
                branchesFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                HEAD.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // create staging area
        StagingArea sa = new StagingArea();
        Utils.writeObject(stagingFile, sa);

        // create initial commit
        Commit initialCommit = new Commit(new HashMap<>(), "initial commit", null);
        File commitFile = join(commits, initialCommit.getID());
        Utils.writeObject(commitFile, initialCommit);

        // set HEAD to point to the 'master' branch
        Utils.writeContents(HEAD, "master");

        // update the head commit of the current branch ('master')
        HashMap<String, String> branches = new HashMap<>();
        branches.put(currBranch(), initialCommit.getID());
        Utils.writeObject(branchesFile, branches);
    }



    public static void add(String fileName) {
        File addFile = join(CWD, fileName);
        if (!addFile.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }

        StagingArea sa = currStagingArea();

        // create blob of file
        byte[] blob = Utils.readContents(addFile);
        String blobID = Utils.sha1(blob);


        // if the file has not changed from the current commit,
        // the file should not be staged for addition
        Commit currentCommit = currCommit();
        if (blobID.equals(currentCommit.getFilesMap().get(fileName))) {
            if (sa.filesToAdd().containsKey(fileName)) {
                sa.filesToAdd().remove(fileName);
                sa.save(stagingFile);
            }
            return;
        }

        // file should not be staged for removal
        if (sa.filesToRemove().contains(fileName)) {
            sa.filesToRemove().remove(fileName);
        }

        // stage file for addition
        sa.add(fileName, blobID);

        // save blob of file to blobs folder
        File blobFile = join(blobs, blobID);
        Utils.writeContents(blobFile, blob);

        // update staging area
        sa.save(stagingFile);

    }



    public static void commit(String message) {
        if (message.equals("")) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }

        StagingArea sa = currStagingArea();
        Commit parentCommit = currCommit();

        if (sa.filesToAdd().isEmpty() && sa.filesToRemove().isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }

        // copy parent files
        HashMap<String, String> commitFiles = new HashMap<>();
        commitFiles.putAll(parentCommit.getFilesMap());

        // update files staged for addition & removal
        commitFiles.putAll(sa.filesToAdd());
        for (String f : sa.filesToRemove()) {
            commitFiles.remove(f);
        }

        sa.clear();
        sa.save(stagingFile);


        Commit newCommit = new Commit(commitFiles, message, parentCommit.getID());

        // save commit to commits folder
        File commitFile = join(commits, newCommit.getID());
        Utils.writeObject(commitFile, newCommit);

        // update branch head
        HashMap<String, String> branches = Utils.readObject(branchesFile, HashMap.class);
        branches.put(currBranch(), newCommit.getID());
        Utils.writeObject(branchesFile, branches);

    }



    public static void rm(String fileName) {
        StagingArea sa = currStagingArea();

        Commit currentCommit = currCommit();

        // file should not be staged for addition
        if (sa.filesToAdd().containsKey(fileName)) {
            sa.filesToAdd().remove(fileName);
            sa.save(stagingFile);
        }


        // stage the file for removal only if it is being tracked in the current commit
        if (currentCommit.getFilesMap().containsKey(fileName)) {
            sa.rm(fileName);
            Utils.restrictedDelete(fileName);
            sa.save(stagingFile);
        }

        else {
            System.out.println("No reason to remove the file.");
        }
    }



    public static void log() {
        Commit c = currCommit();
        while (c != null) {
            System.out.println("===");
            System.out.println("commit " + c.getID());
            System.out.println("Date: " + c.getDate());
            System.out.println(c.getMessage());
            System.out.println();

            if (c.getParent() == null) {
                return;
            }

            // the next commit to print is the current commit's parent
            File f = join(commits, c.getParent());
            c = Utils.readObject(f, Commit.class);
        }
    }

    public static void globalLog() {
        List<String> commitList = Utils.plainFilenamesIn(commits);

        for (String commit : commitList) {
            File commitFile = join(commits, commit);
            Commit c = Utils.readObject(commitFile, Commit.class);

            System.out.println("===");
            System.out.println("commit " + c.getID());
            System.out.println("Date: " + c.getDate());
            System.out.println(c.getMessage());
            System.out.println();
        }
    }

    public static void find(String commitMessage) {
        List<String> commitList = Utils.plainFilenamesIn(commits);
        boolean found = false;

        for (String commit : commitList) {
            File commitFile = join(commits, commit);
            Commit c = Utils.readObject(commitFile, Commit.class);

            if (c.getFilesMap().equals(commitMessage)) {
                System.out.println(c.getID());
                found = true;
            }
        }

        if (!found) {
            System.out.println("Found no commit with that message.");
        }

    }


    public static void status() {
        StagingArea sa = currStagingArea();

        // branches
        System.out.println("=== Branches ===");
        HashMap<String, String> branches = Utils.readObject(branchesFile, HashMap.class);
        for (String b : branches.keySet()) {
            if (b.equals(currBranch())) {
                System.out.println("*" + b);
            }
            else {
                System.out.println(b);
            }
        }
        System.out.println();

        // files staged for addition
        System.out.println("=== Staged Files ===");
        for (String f : sa.filesToAdd().keySet()) {
            System.out.println(f);
        }
        System.out.println();

        // files staged for removal
        System.out.println("=== Removed Files ===");
        for (String f : sa.filesToRemove()) {
            System.out.println(f);
        }
        System.out.println();


        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();

        System.out.println("=== Untracked Files ===");
        System.out.println();
        
    }




    public static void checkoutFile(String fileName) {
        Commit c = currCommit();
        HashMap<String, String> cFilesMap = c.getFilesMap();
        if (!cFilesMap.containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        File f = join(blobs, cFilesMap.get(fileName));
        byte[] b = readContents(f);

        File workingFile = join(CWD, fileName);
        Utils.writeContents(workingFile, b);

    }


    public static void checkoutFile(String commitID, String fileName) {
        File commitFile = join(commits, commitID);
        if (!commitFile.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        Commit c = Utils.readObject(commitFile, Commit.class);
        HashMap<String, String> cFilesMap = c.getFilesMap();
        if (!cFilesMap.containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        File f = join(blobs, cFilesMap.get(fileName));
        byte[] b = readContents(f);
        File workingFile = join(CWD, fileName);
        Utils.writeContents(workingFile, b);

    }



    public static void checkoutBranch(String branchName) {
        StagingArea sa = currStagingArea();

        HashMap<String, String> branches = Utils.readObject(branchesFile, HashMap.class);
        if (!branches.containsKey(branchName)) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        if (branchName.equals(currBranch())) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }

        File c = join(commits, branches.get(branchName));
        Commit checkoutCommit = Utils.readObject(c, Commit.class);
        HashMap<String, String> checkoutFiles = checkoutCommit.getFilesMap();
        Commit currentCommit = currCommit();


        for (String f : Utils.plainFilenamesIn(CWD)) {
            if (!currentCommit.getFilesMap().containsKey(f) && checkoutFiles.containsKey(f)) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
            if (currentCommit.getFilesMap().containsKey(f) && !checkoutFiles.containsKey(f)) {
                Utils.restrictedDelete(f);
            }
        }

        for (String f : checkoutFiles.keySet()) {
            File workingFile = join(CWD, f);
            String blobID = checkoutFiles.get(f);
            File blobFile = join(blobs, blobID);
            byte[] blob = readContents(blobFile);
            Utils.writeContents(workingFile, blob);

        }

        Utils.writeContents(HEAD, branchName);

        sa.clear();
        sa.save(stagingFile);

    }





    private static StagingArea currStagingArea() {
        return Utils.readObject(stagingFile, StagingArea.class);
    }

    private static Commit currCommit() {
        String headBranch = currBranch();
        HashMap<String, String> b = Utils.readObject(branchesFile, HashMap.class);
        String headCommit = b.get(headBranch);
        File f = join(commits, headCommit);
        return Utils.readObject(f, Commit.class);
    }


    private static String currBranch() {
        return readContentsAsString(HEAD);
    }
}
