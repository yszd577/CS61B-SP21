package gitlet;

import java.io.File;
import static gitlet.Utils.*;

public class Blob {
    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The blob directory. */
    public static final File BLOB_DIR = join(GITLET_DIR, "blob");
    /** The blob name. */
    private String name;

    Blob(File fromFile, File toDirectory) {
        String content = readContentsAsString(fromFile);
        name = sha1(content);
        writeContents(join(toDirectory, name), content);
    }

    public String getName() {
        return name;
    }

    public static String getContent(String name) {
        return readContentsAsString(join(BLOB_DIR, name));
    }
}
