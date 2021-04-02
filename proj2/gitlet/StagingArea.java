package gitlet;


import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.io.File;


public class StagingArea implements Serializable {

    private HashMap<String, String> stagedForAddition;
    private HashSet<String> stagedForRemoval;

    public StagingArea() {
        stagedForAddition = new HashMap<>();
        stagedForRemoval = new HashSet<>();
    }

    public void add(String fileName, String blobID) {
        stagedForAddition.put(fileName, blobID);
    }

    public void rm(String file) {
        stagedForRemoval.add(file);
    }

    public void save(File stagingFile) {
        Utils.writeObject(stagingFile, this);
    }

    public HashMap<String, String> filesToAdd() {
        return stagedForAddition;
    }

    public HashSet<String> filesToRemove() {
        return stagedForRemoval;
    }

    public void clear() {
        stagedForAddition = new HashMap<>();
        stagedForRemoval = new HashSet<>();
    }


}

