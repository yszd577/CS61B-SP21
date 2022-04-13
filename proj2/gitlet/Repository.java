package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;


import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Raiden Ei
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The blob directory. */
    public static final File BLOB_DIR = join(GITLET_DIR, "blob");
    /** The commit directory */
    public static final File COMMIT_DIR = join(GITLET_DIR, "commit");

    /** Staged for addition. */
    public static final File ADDITION = join(GITLET_DIR, "addition");
    /** Staged for removal. */
    public static final File REMOVAL = join(GITLET_DIR, "removal");

    /** Head pointer and branch pointer. */
    private static String head;
    private static Map<String, String> branch;
    private static String headSha1;

    /** Pointer file. */
    public static final File HEAD = join(GITLET_DIR, "head");
    public static final File BRANCH = join(GITLET_DIR, "branch");

    /** Stage area map. */
    private static Map<String, String> addMap;
    private static Map<String, String> removeMap;

    /** Commit id set. */
    private static Set<String> commitIdSet;

    /** Commit id set file. */
    public static final File COMMITID = join(GITLET_DIR, "commitId");

    /* TODO: fill in the rest of this class. */


    public static void init() {
        if (GITLET_DIR.exists()) {
            message("A Gitlet version-control system already exists in the current directory.");
        }
        initialization();
        load();
        Commit initialCommit = new Commit();
        File initialCommitFile = join(COMMIT_DIR, "initialCommit");
        writeObject(initialCommitFile, initialCommit);
        String commitSha1 = sha1(readContents(initialCommitFile));
        initialCommitFile.renameTo(join(COMMIT_DIR, commitSha1));
        head = "master";
        branch.put("master", commitSha1);
        commitIdSet.add(commitSha1);
        save();
    }

    private static void initialization() {
        GITLET_DIR.mkdir();
        BLOB_DIR.mkdir();
        COMMIT_DIR.mkdir();
        try {
            ADDITION.createNewFile();
            REMOVAL.createNewFile();
            COMMITID.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        head = "start";
        addMap = new TreeMap<>();
        removeMap = new TreeMap<>();
        commitIdSet = new HashSet<>();
        branch = new HashMap<>();
        save();
    }

    private static void load() {
        head = readObject(HEAD, String.class);
        branch = readObject(BRANCH, HashMap.class);
        headSha1 = branch.get(head);
        addMap = readObject(ADDITION, TreeMap.class);
        removeMap = readObject(REMOVAL, TreeMap.class);
        commitIdSet = readObject(COMMITID, HashSet.class);
    }

    private static void save() {
        writeObject(HEAD, head);
        writeObject(BRANCH, (Serializable) branch);
        writeObject(ADDITION, (Serializable) addMap);
        writeObject(REMOVAL, (Serializable) removeMap);
        writeObject(COMMITID, (Serializable) commitIdSet);
    }

    public static void add(String fileName) {
        if (!GITLET_DIR.exists()) {
            message("Not in an initialized Gitlet directory.");
            return;
        }
        load();
        File addFile = new File(fileName);
        if (!addFile.exists()) {
            message("File does not exist.");
            return;
        }
        removeMap.remove(fileName);
        String fileContent = sha1(readContentsAsString(addFile));
        String existContent = Commit.getCommit(headSha1).getFileMap().get(fileName);
        if (fileContent.equals(existContent)) {
            if (addMap.containsKey(fileName)) {
                addMap.remove(fileName);
            }
        } else {
            writeContents(join(BLOB_DIR, fileContent), readContentsAsString(addFile));
            if (addMap.containsKey(fileName)) {
                restrictedDelete(join(BLOB_DIR, addMap.get(fileName)));
            }
            addMap.put(fileName, fileContent);
        }
        save();
    }

    public static void commit(String msg) {
        if (!GITLET_DIR.exists()) {
            message("Not in an initialized Gitlet directory.");
            return;
        }
        if (msg.length() == 0) {
            message("Please enter a commit message.");
            return;
        }
        load();
        if (addMap.isEmpty() && removeMap.isEmpty()) {
            message("No changes added to the commit.");
            return;
        }
        commitBase(msg, null);
        save();
    }

    public static void rm(String fileName) {
        if (!GITLET_DIR.exists()) {
            message("Not in an initialized Gitlet directory.");
            return;
        }
        load();
        Map<String, String> fileMap = Commit.getCommit(headSha1).getFileMap();
        if (!addMap.containsKey(fileName) && !fileMap.containsKey(fileName)) {
            message("No reason to remove the file.");
            return;
        }
        addMap.remove(fileName);
        if (fileMap.containsKey(fileName)) {
            removeMap.put(fileName, fileMap.get(fileName));
            restrictedDelete(fileName);
        }
        save();
    }

    public static void log() {
        if (!GITLET_DIR.exists()) {
            message("Not in an initialized Gitlet directory.");
            return;
        }
        load();
        Commit current = Commit.getCommit(headSha1);
        while (true) {
            System.out.println("===");
            System.out.println("commit " + headSha1);
            if (current.getAnotherParent() != null) {
                System.out.print("Merge: ");
                System.out.println(current.getParent().substring(0, 7) + " "
                        + current.getAnotherParent().substring(0, 7));
            }
            System.out.println(current.getDate());
            System.out.println(current.getMessage());
            System.out.println();
            headSha1 = current.getParent();
            if (headSha1 == null) {
                break;
            }
            current = Commit.getCommit(headSha1);
        }
    }

    public static void globalLog() {
        if (!GITLET_DIR.exists()) {
            message("Not in an initialized Gitlet directory.");
            return;
        }
        List<String> commitTree = plainFilenamesIn(COMMIT_DIR);
        for (String commitSHA1 : commitTree) {
            Commit current = Commit.getCommit(commitSHA1);
            System.out.println("===");
            System.out.println("commit " + commitSHA1);
            System.out.println(current.getDate());
            System.out.println(current.getMessage());
            System.out.println();
        }
    }

    public static void find(String msg) {
        if (!GITLET_DIR.exists()) {
            message("Not in an initialized Gitlet directory.");
            return;
        }
        boolean flag = false;
        List<String> commitTree = plainFilenamesIn(COMMIT_DIR);
        for (String commitSHA1 : commitTree) {
            Commit current = Commit.getCommit(commitSHA1);
            if (current.getMessage().equals(msg)) {
                System.out.println(commitSHA1);
                flag = true;
            }
        }
        if (!flag) {
            message("Found no commit with that message.");
        }
    }

    public static void status() {
        if (!GITLET_DIR.exists()) {
            message("Not in an initialized Gitlet directory.");
            return;
        }
        load();
        System.out.println("=== Branches ===");
        System.out.println("*" + head);
        for (String branchName : branch.keySet()) {
            if (!branchName.equals(head)) {
                System.out.println(branchName);
            }
        }
        System.out.println();
        Map<String, String> modifyNotStaged = new TreeMap<>();
        System.out.println("=== Staged Files ===");
        for (var addFile : addMap.keySet()) {
            System.out.println(addFile);
            File tempFile = new File(addFile);
            if (!tempFile.exists()) {
                modifyNotStaged.put(addFile, "deleted");
            } else {
                String currentContent = sha1(readContentsAsString(tempFile));
                if (!currentContent.equals(addMap.get(addFile))) {
                    modifyNotStaged.put(addFile, "modified");
                }
            }
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        for (var removeFile : removeMap.keySet()) {
            System.out.println(removeFile);
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        Map<String, String> fileMap = Commit.getCommit(headSha1).getFileMap();
        for (var file : fileMap.keySet()) {
            File tempFile = new File(file);
            if (!tempFile.exists() && !removeMap.containsKey(file)) {
                modifyNotStaged.put(file, "deleted");
            }
            if (tempFile.exists()) {
                String fileContent = sha1(readContentsAsString(tempFile));
                if (!fileContent.equals(fileMap.get(file)) && !addMap.containsKey(file)
                        && !removeMap.containsKey(file)) {
                    modifyNotStaged.put(file, "modified");
                }
            }
        }
        modifyNotStaged.forEach((k, v) -> System.out.println(k + " (" + v + ")"));
        System.out.println();
        System.out.println("=== Untracked Files ===");
        Set<String> untracked = new TreeSet<>();
        List<String> workingFiles = plainFilenamesIn(CWD);
        for (var file : workingFiles) {
            if (!addMap.containsKey(file) && !fileMap.containsKey(file)) {
                untracked.add(file);
            }
            if (removeMap.containsKey(file)) {
                untracked.add(file);
            }
        }
        untracked.forEach(System.out::println);
        System.out.println();
    }

    public static void checkout(String[] args) {
        if (!GITLET_DIR.exists()) {
            message("Not in an initialized Gitlet directory.");
            return;
        }
        load();
        if (args.length == 3) {
            if (!args[1].equals("--")) {
                message("Incorrect operands.");
                return;
            }
            String fileName = args[2];
            checkFileExists(headSha1, fileName);
        } else if (args.length == 4) {
            if (!args[2].equals("--")) {
                message("Incorrect operands.");
                return;
            }
            String commitId = args[1];
            String fileName = args[3];
            if (checkCommitExists(commitId)) {
                checkFileExists(expand(commitId), fileName);
            } else {
                message("No commit with that id exists.");
            }
        } else {
            String givenBranch = args[1];
            if (givenBranch.equals(head)) {
                message("No need to checkout the current branch.");
                return;
            }
            if (!branch.containsKey(givenBranch)) {
                message("No such branch exists.");
                return;
            }
            head = givenBranch;
            resetCommitId(branch.get(head));
            save();
        }
    }

    private static void resetCommitId(String commitId) {
        Map<String, String> currentFileMap = Commit.getCommit(headSha1).getFileMap();
        Map<String, String> givenFileMap = Commit.getCommit(commitId).getFileMap();
        List<String> workingFiles = plainFilenamesIn(CWD);
        for (var file : workingFiles) {
            if (!currentFileMap.containsKey(file) && givenFileMap.containsKey(file)) {
                message("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
                return;
            }
            if (currentFileMap.containsKey(file) && !givenFileMap.containsKey(file)) {
                restrictedDelete(file);
            }
        }
        givenFileMap.forEach((k, v) ->
                writeContents(new File(k), readContentsAsString(join(BLOB_DIR, v))));
        addMap.clear();
        removeMap.clear();
    }

    private static String expand(String commitId) {
        if (commitId.length() == 40) {
            return commitId;
        }
        List<String> commitTree = plainFilenamesIn(COMMIT_DIR);
        for (String commitSha1 : commitTree) {
            if (commitId.equals(commitSha1.substring(0, commitId.length()))) {
                return commitSha1;
            }
        }
        return null;
    }

    private static boolean checkCommitExists(String commitId) {
        if (commitId.length() == 40) {
            return commitIdSet.contains(commitId);
        } else {
            return expand(commitId) != null;
        }
    }

    private static void checkFileExists(String commitId, String fileName) {
        Map<String, String> fileMap = Commit.getCommit(commitId).getFileMap();
        if (!fileMap.containsKey(fileName)) {
            message("File does not exist in that commit.");
        } else {
            String content = readContentsAsString(join(BLOB_DIR, fileMap.get(fileName)));
            writeContents(new File(fileName), content);
        }
    }

    public static void branch(String branchName) {
        if (!GITLET_DIR.exists()) {
            message("Not in an initialized Gitlet directory.");
            return;
        }
        load();
        branch.keySet().forEach(k -> {
            if (k.equals(branchName)) {
                message("A branch with that name already exists.");
            }
        });
        branch.put(branchName, headSha1);
        save();
    }

    public static void rmBranch(String branchName) {
        if (!GITLET_DIR.exists()) {
            message("Not in an initialized Gitlet directory.");
            return;
        }
        load();
        if (!branch.containsKey(branchName)) {
            message("A branch with that name does not exist.");
            return;
        }
        if (head.equals(branchName)) {
            message("Cannot remove the current branch.");
            return;
        }
        branch.remove(branchName);
        save();
    }

    public static void reset(String commitId) {
        if (!GITLET_DIR.exists()) {
            message("Not in an initialized Gitlet directory.");
            return;
        }
        load();
        if (!checkCommitExists(commitId)) {
            message("No commit with that id exists.");
            return;
        }
        resetCommitId(expand(commitId));
        branch.put(head, commitId);
        save();
    }

    public static void merge(String givenBranch) {
        if (!GITLET_DIR.exists()) {
            message("Not in an initialized Gitlet directory.");
            return;
        }
        load();
        if (!addMap.isEmpty() || !removeMap.isEmpty()) {
            message("You have uncommitted changes.");
            return;
        }
        if (!branch.containsKey(givenBranch)) {
            message("A branch with that name does not exist.");
            return;
        }
        if (givenBranch.equals(head)) {
            message("Cannot merge a branch with itself.");
            return;
        }
        String splitSha1 = getSplitCommit(givenBranch);
        if (splitSha1.equals(branch.get(givenBranch))) {
            message("Given branch is an ancestor of the current branch.");
            return;
        }
        if (splitSha1.equals(headSha1)) {
            resetCommitId(branch.get(givenBranch));
            message("Current branch fast-forwarded.");
            return;
        }
        Map<String, String> splitMap = Commit.getCommit(splitSha1).getFileMap();
        Map<String, String> currentMap = Commit.getCommit(headSha1).getFileMap();
        Map<String, String> givenMap = Commit.getCommit(branch.get(givenBranch)).getFileMap();
        Set<String> fileSet = new HashSet<>();
        String splitBlob;
        String givenBlob;
        String currentBlob;
        fileSet.addAll(currentMap.keySet());
        fileSet.addAll(givenMap.keySet());
        for (String name : fileSet) {
            splitBlob = splitMap.get(name);
            givenBlob = givenMap.get(name);
            currentBlob = splitMap.get(name);
            if (splitMap.containsKey(name)) {
                if (givenMap.containsKey(name) && currentMap.containsKey(name)) {
                    if (splitBlob.equals(currentBlob) && !splitBlob.equals(givenBlob)) {
                        writeContents(new File(name), getContent(givenBlob));
                        addMap.put(name, givenBlob);
                    }
                    if (!splitBlob.equals(currentBlob) && !splitBlob.equals(givenBlob)
                        && !currentBlob.equals(givenBlob)) {
                        mergeContent(name, currentBlob, givenBlob);
                    }
                } else if (currentMap.containsKey(name)) {
                    if (splitBlob.equals(currentBlob)) {
                        restrictedDelete(name);
                        removeMap.put(name, currentBlob);
                    }
                    if (!splitBlob.equals(currentBlob)) {
                        mergeContent(name, currentBlob, givenBlob);
                    }
                } else if (givenMap.containsKey(name) && !splitBlob.equals(givenBlob)) {
                    mergeContent(name, currentBlob, givenBlob);
                }
            } else {
                if (givenMap.containsKey(name) && !currentMap.containsKey(name)) {
                    givenBlob = givenMap.get(name);
                    writeContents(new File(name), getContent(givenBlob));
                    addMap.put(name, givenBlob);
                }
                if (givenMap.containsKey(name) && currentMap.containsKey(name)) {
                    if (!givenBlob.equals(currentBlob)) {
                        mergeContent(name, currentBlob, givenBlob);
                    }
                }
            }
        }
        commitBase("Merged " + givenBranch + " into " + head + ".", branch.get(givenBranch));
        save();
    }

    private static void commitBase(String msg, String another) {
        Commit current = new Commit(msg, headSha1);
        current.setAnotherParent(another);
        if (!addMap.isEmpty()) {
            addMap.forEach(current::put);
        }
        addMap.clear();
        if (!removeMap.isEmpty()) {
            removeMap.keySet().forEach(current::remove);
        }
        removeMap.clear();
        File tempFile = join(COMMIT_DIR, "tempCommit");
        writeObject(tempFile, current);
        String commitSha1 = sha1(readContents(tempFile));
        tempFile.renameTo(join(COMMIT_DIR, commitSha1));
        headSha1 = commitSha1;
        commitIdSet.add(headSha1);
        branch.put(head, headSha1);
    }

    private static String getContent(String name) {
        File file = join(BLOB_DIR, name);
        if (!file.exists()) {
            return "";
        }
        return readContentsAsString(file);
    }

    private static String getSplitCommit(String givenBranch) {
        Set<String> hashSet = new HashSet<>();
        String commitSha1 = branch.get(givenBranch);
        while (true) {
            Commit current = Commit.getCommit(commitSha1);
            hashSet.add(commitSha1);
            if (current.getParent() == null) {
                break;
            }
            commitSha1 = current.getParent();
        }
        commitSha1 = headSha1;
        while (true) {
            Commit current = Commit.getCommit(commitSha1);
            if (hashSet.contains(commitSha1)) {
                return commitSha1;
            }
            commitSha1 = current.getParent();
        }
    }


    private static void mergeContent(String name, String currentBlob, String givenBob) {
        StringBuilder sb = new StringBuilder();
        sb.append("<<<<<<< HEAD\n");
        sb.append(getContent(givenBob));
        sb.append("\n");
        sb.append("=======\n");
        sb.append(getContent(givenBob));
        sb.append("\n");
        sb.append(">>>>>>>");
        sb.append("\n");
        String content = sb.toString();
        writeContents(new File(name), sb.toString());
        String blobSha1 = sha1(content);
        writeContents(join(BLOB_DIR, blobSha1), content);
        addMap.put(name, content);
        System.out.println("Encountered a merge conflict.");
    }
}


