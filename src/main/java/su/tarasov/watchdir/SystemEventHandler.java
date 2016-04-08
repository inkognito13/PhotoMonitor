package su.tarasov.watchdir;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
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

    private FileProcessor fileProcessor;
    private Configuration configuration;

    private Logger logger = LoggerFactory.getLogger(SystemEventHandler.class);
    
    public SystemEventHandler(Configuration configuration) {
        this.configuration = configuration;
        this.fileProcessor = new FileProcessor(configuration);
    }

    public void handleSystemChangeEvent(Path file, WatchEvent.Kind kind) {
        if (kind.equals(ENTRY_CREATE)) {
            entryCreate(file);
        } else if (kind.equals(ENTRY_DELETE)) {

        }
    }

    private void entryCreate(Path file) {
        if (FileExtensionUtil.isFileRaw(file, configuration.RAW_EXTENSION)) {
            logger.debug("File {} is Raw Photo file. Converting and uploading", file);
            String thumb = fileProcessor.createThumb(file.toString());
            fileProcessor.uploadToPhotoFolder(thumb);
            fileProcessor.uploadToRawFolder(file.toString());
        } else if (Files.isDirectory(file, NOFOLLOW_LINKS)) {
            logger.debug("File {} is directory. Creating new dir on remote drive", file);
            fileProcessor.createRemoteDirectories(file.toString());
        } else {
            logger.debug("File {} is regular file. Uploading to remote drive", file);
            fileProcessor.uploadToPhotoFolder(file.toString());
        }
    }
}
