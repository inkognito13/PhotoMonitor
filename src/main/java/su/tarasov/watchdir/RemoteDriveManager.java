package su.tarasov.watchdir;

/**
 * @author Dmitry Tarasov
 *         Date: 03/05/2016
 *         Time: 13:18
 */
public class RemoteDriveManager {
    private String acdCliPath;
    private ShellExecutor shellExecutor;

    public RemoteDriveManager(String acdCliPath) {
        this.acdCliPath = acdCliPath;
        this.shellExecutor = new ShellExecutor();
    }

    public boolean createDir(String dirPath) {
        String command = acdCliPath + " mkdir  --parents " + dirPath;
        return shellExecutor.executeCommand(command);
    }
    
    public boolean uploadFile(String localFile,String remoteDir){
        String command = acdCliPath + " upload "+localFile+" "+remoteDir;
        return shellExecutor.executeCommand(command);
    }
}
