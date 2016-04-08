package su.tarasov.watchdir;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitry Tarasov
 *         Date: 03/05/2016
 *         Time: 13:18
 */
public class RemoteDriveManager {
    private String acdCliPath;
    private ShellExecutor shellExecutor;
    private boolean firstRun = true;

    private static int MAX_RETRIES = 5;
    private static int MAX_CONNECTIONS = 5;

    public RemoteDriveManager(String acdCliPath) {
        this.acdCliPath = acdCliPath;
        this.shellExecutor = new ShellExecutor();
    }

    public boolean createDir(String dirPath) {
        if (syncIfNeeded()) {
            String[] command = {acdCliPath, "mkdir", "--parents", dirPath};
            return shellExecutor.executeCommand(command);
        } else {
            return false;
        }
    }

    public boolean uploadFile(String localFile, String remoteDir, String excludeEnging) {
        if (syncIfNeeded()) {
            List<String> command = new ArrayList<String>();
            command.add(acdCliPath);
            command.add("upload");
            if (excludeEnging != null && !excludeEnging.isEmpty()) {
                command.add("--exclude-ending");
                command.add(excludeEnging);
            }
            command.add("--max-connections");
            command.add(Integer.toString(MAX_CONNECTIONS));
            command.add("--max-retries");
            command.add(Integer.toString(MAX_RETRIES));
            command.add(localFile);
            command.add(remoteDir);
            return shellExecutor.executeCommand(command.toArray(new String[command.size()]));
        } else {
            return false;
        }
    }

    private boolean syncIfNeeded() {
        if (firstRun) {
            if (sync()) {
                firstRun = false;
            }
            return true;
        } else {
            return true;
        }
    }

    private boolean sync() {
        String[] command = {acdCliPath, "sync"};
        return shellExecutor.executeCommand(command);
    }
}
