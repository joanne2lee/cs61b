package gitlet;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;


public class StagingArea implements Serializable {

    private HashMap<String, String> stagedForAddition;
    private ArrayList<String> stagedForRemoval;

    public StagingArea() {
        stagedForAddition = new HashMap<>();
        stagedForRemoval = new ArrayList<>();
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

    public ArrayList<String> filesToRemove() {
        return stagedForRemoval;
    }

    public void clear() {
        stagedForAddition = new HashMap<>();
        stagedForRemoval = new ArrayList<>();
    }


}

