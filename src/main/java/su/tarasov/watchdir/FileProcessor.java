package su.tarasov.watchdir;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

/**
 * @author Dmitry Tarasov
 *         Date: 03/22/2016
 *         Time: 11:06
 */
public class FileProcessor {

    private ThumbMaker thumbMaker;
    private RemoteDriveManager remoteDriveManager;
    private Configuration configuration;

    private Logger logger = LoggerFactory.getLogger(FileProcessor.class);

    public FileProcessor(Configuration configuration) {
        this.configuration = configuration;
        this.thumbMaker = new ThumbMaker(configuration.DCRAW_COMMAND);
        this.remoteDriveManager = new RemoteDriveManager(configuration.ACDCLI_COMMAND);
    }

    public String createThumb(String rawFileName) {
        String thumbFileName = convert(rawFileName);
        if (thumbFileName != null) {
            logger.debug("Thumb {} is created", thumbFileName);
        }
        return thumbFileName;
    }
    
    public void uploadToPhotoFolder(String localFileName) {
        uploadToPhotoFolder(localFileName,null);
    }
    
    public void uploadToPhotoFolder(String localFileName, String excludeEnding) {
        String remoteDir = getFolder(localFileName).replace(configuration.BASE_FOLDER, configuration.PHOTOS_REMOTE_FOLDER)
                .replace(configuration.THUMB_FOLDER, configuration.PHOTOS_REMOTE_FOLDER);
        logger.debug("Uploading file {} to remote drive PHOTO folder {}", localFileName, remoteDir);
        boolean fileUploaded = remoteDriveManager.uploadFile(localFileName, remoteDir, excludeEnding);
        if (fileUploaded) {
            logger.debug("File {} successfully uploaded to remote drive PHOTO folder {}", localFileName, remoteDir);
        } else {
            logger.error("Thumb {} NOT uploaded to remote drive PHOTO folder {}", localFileName, remoteDir);
        }
    }

    public void uploadToRawFolder(String localFileName) {
        uploadToRawFolder(localFileName,null);
    }
    
    public void uploadToRawFolder(String localFileName, String excludeEnding) {
        String remoteDir = getFolder(localFileName).replace(configuration.BASE_FOLDER, configuration.RAW_REMOTE_FOLDER);
        boolean fileUploaded = remoteDriveManager.uploadFile(localFileName, remoteDir, excludeEnding);
        if (fileUploaded) {
            logger.debug("File {} successfully uploaded to remote drive RAW folder {}", localFileName, remoteDir);
        } else {
            logger.error("Regular file {} NOT uploaded to remote drive RAW folder {}", localFileName, remoteDir);
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

    public void createRemoteDirectories(String localDir) {
        String remoteRawDir = localDir.replace(configuration.BASE_FOLDER, configuration.RAW_REMOTE_FOLDER);
        boolean success = remoteDriveManager.createDir(remoteRawDir);
        if (success) {
            logger.debug("Remote RAW directory {} is created", remoteRawDir);
        } else {
            logger.error("Remote RAW directory is NOT created");
            return;
        }

        String remotePhotosDir = localDir.replace(configuration.BASE_FOLDER, configuration.PHOTOS_REMOTE_FOLDER);
        success = remoteDriveManager.createDir(remotePhotosDir);
        if (success) {
            logger.debug("Remote Photos directory {} is created", remoteRawDir);
        } else {
            logger.error("Remote Photos directory is NOT created");
        }
    }

    private String getFolder(String fileName) {
        return Paths.get(fileName).getParent().toString();
    }
}
