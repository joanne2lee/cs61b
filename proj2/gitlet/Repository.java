package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.*;

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
    static File stagingFile = join(GITLET_DIR, "stagingArea.txt");

    /** A folder containing commits, files named by commit IDs. */
    static File commits = join(GITLET_DIR, "commits");

    /** A folder containing blobs, files named by blob IDs. */
    static File blobs = join(GITLET_DIR, "blobs");

    /** A file tracking the branches as a HashMap, where
     * keys = the branch name, and
     * values = last commit (head) of the branch. */
    static File branchesFile = join(GITLET_DIR, "branches.txt");

    /** A file tracking the HEAD branch. */
    static File HEAD = join(GITLET_DIR, "HEAD.txt");





    public static void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        } else {
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

        // file should not be staged for removal
        if (sa.filesToRemove().contains(fileName)) {
            sa.filesToRemove().remove(fileName);
        }

        // create blob of file
        byte[] blob = Utils.readContents(addFile);
        String blobID = Utils.sha1(blob);


        // if the file has not changed from the current commit,
        // the file should not be staged for addition
        Commit currentCommit = currCommit();
        if (blobID.equals(currentCommit.getFilesMap().get(fileName))) {
            if (sa.filesToAdd().containsKey(fileName)) {
                sa.filesToAdd().remove(fileName);
            }
            sa.save(stagingFile);
            return;
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

        // stage the file for removal only if it is being tracked in the current commit
        } else if (currentCommit.getFilesMap().containsKey(fileName)) {
            sa.rm(fileName);
            Utils.restrictedDelete(fileName);
            sa.save(stagingFile);
        } else {
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
            c = getCommit(c.getParent());
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

            if (c.getMessage().equals(commitMessage)) {
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
        HashMap<String, String> b = Utils.readObject(branchesFile, HashMap.class);
        ArrayList<String> branches = new ArrayList(b.keySet());
        Collections.sort(branches);
        for (String branch : branches) {
            if (branch.equals(currBranch())) {
                System.out.println("*" + branch);
            } else {
                System.out.println(branch);
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

        Commit checkoutCommit = getCommit(branches.get(branchName));
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

    public static void branch(String branchName) {
        HashMap<String, String> branches = Utils.readObject(branchesFile, HashMap.class);
        if (branches.containsKey(branchName)) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        branches.put(branchName, currCommit().getID());
        Utils.writeObject(branchesFile, branches);
    }

    public static void rmBranch(String branchName) {
        if (currBranch().equals(branchName)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        HashMap<String, String> branches = Utils.readObject(branchesFile, HashMap.class);
        if (!(branches.containsKey(branchName))) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        branches.remove(branchName);
        Utils.writeObject(branchesFile, branches);
    }



    public static void reset(String commitID) {
        File c = join(commits, commitID);
        if (!c.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }

        String currentBranch = currBranch();
        HashMap<String, String> branches = Utils.readObject(branchesFile, HashMap.class);

        // create a temporary branch with the given commit as its head commit
        branches.put("temp", commitID);
        Utils.writeObject(branchesFile, branches);

        // checkout the branch ( = checkout all files in the branch's head commit)
        checkoutBranch("temp");

        // set current branch's head to commit
        // delete temp branch
        // reverse HEAD change done by checkoutBranch
        branches.put(currentBranch, commitID);
        branches.remove("temp");
        Utils.writeContents(HEAD, currentBranch);
        Utils.writeObject(branchesFile, branches);
    }


    public static void merge(String branchName) {
        HashMap<String, String> branches = Utils.readObject(branchesFile, HashMap.class);
        StagingArea sa = currStagingArea();

        if (!sa.filesToAdd().isEmpty() || !sa.filesToRemove().isEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        if (!branches.containsKey(branchName)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if (currBranch().equals(branchName)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }

        File givenF = join(commits, branches.get(branchName));
        Commit given = Utils.readObject(givenF, Commit.class);

        Commit current = currCommit();

        for (String f : Utils.plainFilenamesIn(CWD)) {
            if (!current.getFilesMap().containsKey(f) && given.getFilesMap().containsKey(f)) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }

        // the split point of the two branches
        String sp = findSplitPoint(given.getID());
        File spF = join(commits, sp);
        Commit splitPoint = Utils.readObject(spF, Commit.class);

        if (splitPoint.getID().equals(given.getID())) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }

        if (splitPoint.getID().equals(current.getID())) {
            checkoutBranch(branchName);
            System.out.println("Current branch fast-forwarded.");
            return;
        }

        HashMap<String, String> currentFiles = current.getFilesMap();
        HashMap<String, String> givenFiles = given.getFilesMap();
        HashMap<String, String> splitFiles = splitPoint.getFilesMap();

        // files that have been modified in the given branch since the split point
        ArrayList<String> modifiedInGiven = new ArrayList<>();
        for (String f : splitFiles.keySet()) {
            if (givenFiles.containsKey(f)) {
                if (!givenFiles.get(f).equals(splitFiles.get(f))) {
                    modifiedInGiven.add(f);
                }
            }
        }

        // files that have been modified in the current branch since the split point
        ArrayList<String> modifiedInCurrent = new ArrayList<>();
        for (String f : splitFiles.keySet()) {
            if (currentFiles.containsKey(f)) {
                if (!currentFiles.get(f).equals(splitFiles.get(f))) {
                    modifiedInCurrent.add(f);
                }
            }
        }


        for (String f : modifiedInGiven) {
            if (currentFiles.containsKey(f) && !modifiedInCurrent.contains(f)) {
                checkoutFile(given.getID(), f);
            }
        }

        for (String f : givenFiles.keySet()) {
            if (!splitFiles.containsKey(f) && !currentFiles.containsKey(f)) {
                checkoutFile(given.getID(), f);
                add(f);
            }
        }

        for (String f : splitFiles.keySet()) {
            if (!modifiedInCurrent.contains(f) && !givenFiles.containsKey(f)) {
                rm(f);
            }
        }

        boolean inConflict = false;



        // CONFLICT IF:
        // file was present at split point and
            // file modified differently at both branches,
            // file modified at one branch and deleted at other
        for (String f : splitFiles.keySet()) {
            if (modifiedInCurrent.contains(f) && modifiedInGiven.contains(f)
                && !givenFiles.get(f).equals(currentFiles.get(f))) {
                inConflict = true;
                mergeHelper(f, currentFiles.get(f), givenFiles.get(f));
            } else if (modifiedInCurrent.contains(f) && !givenFiles.containsKey(f)) {
                inConflict = true;
                mergeHelper(f, currentFiles.get(f), givenFiles.get(f));
            } else if (modifiedInGiven.contains(f) && !currentFiles.containsKey(f)) {
                inConflict = true;
                mergeHelper(f, currentFiles.get(f), givenFiles.get(f));
            }
        }

        // file was absent at split point and has different versions at branches
        for (String f : currentFiles.keySet()) {
            if (givenFiles.containsKey(f) && !splitFiles.containsKey(f)) {
                if (!currentFiles.get(f).equals(givenFiles.get(f))) {
                    inConflict = true;
                    mergeHelper(f, currentFiles.get(f), givenFiles.get(f));
                }
            }
        }
        commit("Merged " + branchName + " into " + currBranch() + ".");
        if (inConflict) {
            System.out.println("Encountered a merge conflict.");
        }
    }


    private static void mergeHelper(String fileName, String currBlob, String givenBlob) {
        String cont = "<<<<<<< HEAD\n";

        if (currBlob == null) {
            cont += "";
        } else {
            File currF = join(blobs, currBlob);
            cont += Utils.readContentsAsString(currF);
        }
        cont += "=======\n";

        if (givenBlob == null) {
            cont += "";
        } else {
            File givenF = join(blobs, givenBlob);
            cont += Utils.readContentsAsString(givenF);
        }

        cont += ">>>>>>>\n";

        File merged = join(CWD, fileName);
        Utils.writeContents(merged, cont);
    }



    private static String findSplitPoint(String givenHead) {

        ArrayList<String> givenAncestors = new ArrayList<>();

        ArrayDeque<String> gQueue = new ArrayDeque<>();
        gQueue.addLast(givenHead);
        while (!gQueue.isEmpty()) {
            String gCommit = gQueue.removeFirst();
            givenAncestors.add(gCommit);
            Commit c = getCommit(gCommit);
            if (c.getParent() != null) {
                gQueue.addLast(c.getParent());
            }
            if (c.secondParent() != null) {
                gQueue.addLast(c.secondParent());
            }
        }

        ArrayDeque<String> cQueue = new ArrayDeque<>();
        cQueue.addLast(currCommit().getID());
        while (!cQueue.isEmpty()) {
            String cCommit = cQueue.removeFirst();
            if (givenAncestors.contains(cCommit)) {
                return cCommit;
            }
            Commit c = getCommit(cCommit);
            if (c.getParent() != null) {
                cQueue.addLast(c.getParent());
            }
            if (c.secondParent() != null) {
                cQueue.addLast(c.secondParent());
            }
        }
        return null;
    }




    private static Commit getCommit(String commitID) {
        File commitFile = join(commits, commitID);
        Commit commit = Utils.readObject(commitFile, Commit.class);
        return commit;
    }


    private static StagingArea currStagingArea() {
        return Utils.readObject(stagingFile, StagingArea.class);
    }

    private static Commit currCommit() {
        String headBranch = currBranch();
        HashMap<String, String> branches = Utils.readObject(branchesFile, HashMap.class);
        String headCommit = branches.get(headBranch);
        return getCommit(headCommit);
    }

    private static String currBranch() {
        return readContentsAsString(HEAD);
    }
}


