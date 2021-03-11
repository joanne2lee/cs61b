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
    private final String commitID;

    /** The HashMap of files this Commit tracks, where
     * key = file name, and
     * value = hash ID of file blob. */
    private final HashMap<String, String> filesMap;


    /** The message of this Commit. */
    private final String message;

    /** The SHA-1 hash of the parent of this Commit. */
    private final String parentID;

    /** The timestamp of this Commit. */
    private final String timestamp;



    public Commit(HashMap<String, String> filesMap, String message, String parentID) {
        this.filesMap = filesMap;
        this.message = message;
        this.parentID = parentID;


        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
        if (message.equals("initial commit")) {
            Calendar c = Calendar.getInstance();
            c.setTimeZone(TimeZone.getTimeZone("UTC"));
            c.set(1970, 1, 1);
            this.timestamp = sdf.format(c.getTime());
        } else {
            Date commitTime = new Date();
            this.timestamp = sdf.format(commitTime);
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
