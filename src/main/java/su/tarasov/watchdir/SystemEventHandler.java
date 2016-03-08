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
            logger.debug("File {} is Raw Photo file. Converting and uploading", file);
            convertAndUpload(file.toString());
        } else if (Files.isDirectory(file, NOFOLLOW_LINKS)) {
            logger.debug("File {} is directory. Creating new dir on remote drive", file);
            createRemoteDirectories(file.toString());
        } else {
            logger.debug("File {} is regular file. Uploading remote drive", file);
            uploadRegularFile(file.toString());
        }
    }

    private void uploadRegularFile(String localFileName) {
        String remoteDir = getFolder(localFileName).replace(configuration.BASE_FOLDER, configuration.PHOTOS_REMOTE_FOLDER);
        boolean fileUploaded = remoteDriveManager.uploadFile(localFileName, remoteDir);
        if (fileUploaded) {
            logger.debug("Regular file {} successfully uploaded to remote drive folder {}", localFileName, remoteDir);
        } else {
            logger.error("Regular file {} NOT uploaded to remote drive folder {}", localFileName, remoteDir);
        }
    }

    private void convertAndUpload(String rawFileName) {
        String thumbFileName = convert(rawFileName);
        if (thumbFileName != null) {
            logger.debug("Thumb {} is created", thumbFileName);
            String thumbRemoteDir = getFolder(rawFileName).replace(configuration.BASE_FOLDER, configuration.PHOTOS_REMOTE_FOLDER);
            logger.debug("Uploading thumb {} to remote drive folder {}", thumbFileName, thumbRemoteDir);
            boolean thumbUploaded = remoteDriveManager.uploadFile(thumbFileName, thumbRemoteDir);
            if (thumbUploaded) {
                logger.debug("Thumb {} successfully uploaded to remote drive folder {}", thumbFileName, thumbRemoteDir);
            } else {
                logger.error("Thumb {} NOT uploaded to remote drive folder {}", thumbFileName, thumbRemoteDir);
            }
            String rawRemoteDir = getFolder(rawFileName).replace(configuration.BASE_FOLDER, configuration.RAW_REMOTE_FOLDER);
            logger.debug("Uploading raw {} to remote drive folder {}", rawFileName, rawRemoteDir);
            boolean rawUploaded = remoteDriveManager.uploadFile(rawFileName, rawRemoteDir);
            if (rawUploaded) {
                logger.debug("Raw {} successfully uploaded to remote drive folder {}", rawFileName, rawRemoteDir);
            } else {
                logger.error("Raw {} NOT uploaded to remote drive folder {}", rawFileName, rawRemoteDir);
            }
        } else {
            logger.error("Thumb for file {} is not created ", rawFileName);
        }
    }

    private String convert(String rawFileName) {
        String thumbFileName = rawFileName.replace(configuration.BASE_FOLDER, configuration.THUMB_FOLDER)
                .replace(configuration.RAW_EXTENSION, configuration.THUMB_EXTENSION)
                .replace(configuration.RAW_EXTENSION.toLowerCase(), configuration.THUMB_EXTENSION);
        boolean thumbCreated = thumbMaker.createThumb(rawFileName, thumbFileName);
        if (thumbCreated) {
            return thumbFileName;
        } else {
            return null;
        }
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

    private String getFolder(String fileName) {
        return Paths.get(fileName).getParent().toString();
    }
}
