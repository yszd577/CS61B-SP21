package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  does at a high level.
 *
 *  @author Raiden Ei
 */
public class Commit implements Serializable {
    /** List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The commit directory */
    public static final File COMMIT_DIR = join(CWD, ".gitlet", "commit");

    /** The message of this Commit. */
    private String message;
    /** The timestamp of this Commit. */
    private Date timestamp;
    /** The first parent SHA1 of this Commit. */
    private String parent;
    /** The first pointer to parent of this Commit. */
    private transient Commit parent1;
    /** The second parent SHA1 of this Commit. */
    private String anotherParent;
    /** The map between name and SHA1 of content. */
    private Map<String, String> fileMap;


    /** The initial commit. */
    Commit() {
        message = "initial commit";
        timestamp = new Date(0);
        fileMap = new HashMap<>();
        parent = null;
    }

    /** The ordinary commit. */
    Commit(String msg, String head) {
        message = msg;
        timestamp = new Date();
        parent = head;
        File parentCommitSha1 = join(COMMIT_DIR, head);
        Commit parentCommit = readObject(parentCommitSha1, Commit.class);
        parent1 = parentCommit;
        fileMap = parentCommit.fileMap;
    }

    /** Return file map in the commit. */
    public Map<String, String> getFileMap() {
        return fileMap;
    }

    /** Insert or modify key-vale pair in tracked file. */
    public void put(String key, String value) {
        fileMap.put(key, value);
    }

    /** Remove key-vale pair in tracked file. */
    public void remove(String key) {
        fileMap.remove(key);
    }

    /** Return commit by the commitId. */
    public static Commit getCommit(String commitId) {
        File commitSha1 = join(COMMIT_DIR, commitId);
        return readObject(commitSha1, Commit.class);
    }

    /** Return date in format in the commit. */
    public String getDate() {
        return String.format("Date: %1$ta %1$tb %1$te %1$tT %1$tY %1$tz", timestamp);
    }

    /** Return message in the commit. */
    public String getMessage() {
        return message;
    }

    /** Return parent SHA1 in the commit. */
    public String getParent() {
        return parent;
    }

    /** Return another parent SHA1 in the commit. */
    public String getAnotherParent() {
        return anotherParent;
    }

    public void setAnotherParent(String another) {
        anotherParent = another;
    }
}
