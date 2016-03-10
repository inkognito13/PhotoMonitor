package su.tarasov.watchdir;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitry Tarasov
 *         Date: 03/09/2016
 *         Time: 20:48
 */
public class BatchManager {

    private Configuration configuration;
    private SystemEventHandler systemEventHandler;

    private Logger logger = LoggerFactory.getLogger(BatchManager.class);

    public BatchManager(Configuration configuration) {
        this.configuration = configuration;
        this.systemEventHandler = new SystemEventHandler(configuration);
    }

    public void runCompleteBatch() {
        runBatch(configuration.BASE_FOLDER);
    }

    public void runFolderBatch(String folderPath) {
        runBatch(folderPath);
    }

    private void runBatch(String folderPath) {
        List<Path> files = getFolderContent(new File(folderPath));

        logger.debug("Found {} files to proceed", files.size());

        int count = 1;

        for (Path path : files) {
            logger.debug("Processing {} file of {}", count, files.size());
            systemEventHandler.handleSystemChangeEvent(path, StandardWatchEventKinds.ENTRY_CREATE);
            count++;
        }
    }

    private List<Path> getFolderContent(File folder) {
        List<Path> fileList = new ArrayList<Path>();
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                fileList.add(Paths.get(file.toURI()));
                fileList.addAll(getFolderContent(file));
            } else {
                fileList.add(Paths.get(file.toURI()));
            }
        }
        return fileList;
    }


}
