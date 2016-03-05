package su.tarasov.watchdir;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;

/**
 * @author Dmitry Tarasov
 *         Date: 03/05/2016
 *         Time: 00:16
 */
public class SystemEventHandler {

    private ThumbMaker thumbMaker;
    private RemoteDriveManager remoteDriveManager;
    private Configuration configuration;

    private Logger logger = LoggerFactory.getLogger(SystemEventHandler.class);
    
    public SystemEventHandler(Configuration configuration) {
        this.configuration = configuration;
        this.thumbMaker = new ThumbMaker(configuration.DCRAW_COMMAND);
        this.remoteDriveManager = new RemoteDriveManager(configuration.ACDCLI_COMMAND);
    }

    public void handleSystemChangeEvent(Path file, WatchEvent.Kind kind) {
        if (kind.equals(ENTRY_CREATE)) {
            entryCreate(file);
        } else if (kind.equals(ENTRY_DELETE)) {

        }
    }

    private void entryCreate(Path file) {
        if (isFileRaw(file)) {
            logger.debug("File {} is Raw Photo file. Converting", file);
            Path thumb = convert(file);
            logger.debug("Thumb {} is created", thumb);
        } else if (Files.isDirectory(file, NOFOLLOW_LINKS)) {
            logger.debug("File {} is directory. Creating new dir on remote drive", file);
            createRemoteDirectories(file.toString());
        }
    }
    
    private Path convert(Path rawPhotoFile) {
        String original = rawPhotoFile.toString();
        String thumb = original.replace(configuration.BASE_FOLDER, configuration.THUMB_FOLDER)
                .replace(configuration.RAW_EXTENSION, configuration.THUMB_EXTENSION)
                .replace(configuration.RAW_EXTENSION.toLowerCase(), configuration.THUMB_EXTENSION);
        thumbMaker.createThumb(original, thumb);
        return Paths.get(thumb);
    }

    private void createRemoteDirectories(String localDir) {
        String remoteRawDir = localDir.replace(configuration.BASE_FOLDER,configuration.RAW_REMOTE_FOLDER);
        boolean success = remoteDriveManager.createDir(remoteRawDir);
        if (success){
            logger.debug("Remote RAW directory {} is created", remoteRawDir);
        }else{
            logger.error("Remote RAW directory is NOT created");
            return;
        }
         
        String remotePhotosDir = localDir.replace(configuration.BASE_FOLDER,configuration.PHOTOS_REMOTE_FOLDER);
        success = remoteDriveManager.createDir(remotePhotosDir);
        if (success){
            logger.debug("Remote Photos directory {} is created", remoteRawDir);
        }else{
            logger.error("Remote Photos directory is NOT created");
        }
    }

    private boolean isFileRaw(Path file) {
        return (getFileExtension(file.toString()).equalsIgnoreCase(configuration.RAW_EXTENSION));
    }

    private String getFileExtension(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

        if (i > p) {
            extension = fileName.substring(i + 1);
        }
        return extension;
    }
}
