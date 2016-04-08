package su.tarasov.watchdir;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitry Tarasov
 *         Date: 03/09/2016
 *         Time: 20:48
 */
public class BatchManager {

    private Configuration configuration;
    private FileProcessor fileProcessor;

    private Logger logger = LoggerFactory.getLogger(BatchManager.class);

    public BatchManager(Configuration configuration) {
        this.configuration = configuration;
        this.fileProcessor = new FileProcessor(configuration);
    }

    public void runCompleteBatch() {
        runBatch(configuration.BASE_FOLDER);
    }

    public void runFolderBatch(String folderPath) {
        runBatch(folderPath);
    }

    private void runBatch(String folderPath) {
        FolderScanResult files = getFolderContent(new File(folderPath));

        logger.debug("Found {} files to proceed", files.getOverallFiles());
        logger.debug("Creating thumbs for {} raw files", files.raws.size());
        logger.debug("Creating thumbs");

        int rawCount = files.raws.size();

        for (int i = 0; i < rawCount; i++) {
            logger.debug("Processing {} file of {}", i + 1, rawCount);
            fileProcessor.createThumb(files.raws.get(i).toString());
        }

        logger.debug("Thumbs created. Uploading");

        String rootThumbFolder = folderPath.replace(configuration.BASE_FOLDER, configuration.THUMB_FOLDER);
        fileProcessor.uploadToPhotoFolder(rootThumbFolder);

        logger.debug("Thumbs uploaded");
        logger.debug("Uploading non-raw files");
        fileProcessor.uploadToPhotoFolder(folderPath, configuration.RAW_EXTENSION);
        logger.debug("Non-raw files uploaded");
        fileProcessor.uploadToRawFolder(folderPath);
    }

    private FolderScanResult getFolderContent(File folder) {
        FolderScanResult fileList = new FolderScanResult();
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                fileList.addAll(getFolderContent(file));
            } else {
                Path filePath = Paths.get(file.toURI());
                if (FileExtensionUtil.isFileRaw(filePath, configuration.RAW_EXTENSION)) {
                    fileList.raws.add(filePath);
                } else {
                    fileList.regular.add(filePath);
                }
            }
        }
        return fileList;
    }

    private class FolderScanResult {
        List<Path> raws = new ArrayList<Path>();
        List<Path> regular = new ArrayList<Path>();

        int getOverallFiles() {
            return raws.size() + regular.size();
        }

        void addAll(FolderScanResult input) {
            raws.addAll(input.raws);
            regular.addAll(input.regular);
        }
    }


}
