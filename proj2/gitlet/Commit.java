package gitlet;


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Joanne Lee
 */
public class Commit implements Serializable {

    /** The SHA-1 hash of this commit. */
    private String commitID;

    /** The HashMap of files this Commit tracks, where
     * key = file name, and
     * value = hash ID of file blob. */
    private HashMap<String, String> filesMap;


    /** The message of this Commit. */
    private String message;

    /** The SHA-1 hash of this commit's parent commit. */
    private String parentID;

    /** The timestamp of this Commit. */
    private String timestamp;



    public Commit(HashMap<String, String> filesMap, String message, String parentID) {
        this.filesMap = filesMap;
        this.message = message;
        this.parentID = parentID;


        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");

        Date commitTime = new Date();
        this.timestamp = sdf.format(commitTime);

        if (message == "initial commit") {
            Calendar c = Calendar.getInstance();
            c.setTimeZone(TimeZone.getTimeZone("UTC"));
            c.set(1970, 1, 1);
            this.timestamp = sdf.format(c.getTime());
        }

        this.commitID = Utils.sha1(Utils.serialize(this));
    }

    public String getID() {
        return this.commitID;
    }

    public HashMap<String, String> getFilesMap() {
        return this.filesMap;
    }


    public String getMessage() {
        return this.message;
    }

    public String getDate() {
        return this.timestamp;
    }

    public String getParent() {
        return this.parentID;
    }

}
