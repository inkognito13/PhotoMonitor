package su.tarasov.watchdir;

/**
 * @author Dmitry Tarasov
 *         Date: 03/05/2016
 *         Time: 13:18
 */
public class RemoteDriveManager {
    private String acdCliPath;
    private ShellExecutor shellExecutor;
    private boolean firstRun = true;

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
    
    public boolean uploadFile(String localFile,String remoteDir){
        if (syncIfNeeded()) {
            String[] command = {acdCliPath, "upload", localFile, remoteDir};
            return shellExecutor.executeCommand(command);
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
