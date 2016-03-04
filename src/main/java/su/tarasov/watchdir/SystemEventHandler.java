package su.tarasov.watchdir;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;

/**
 * @author Dmitry Tarasov
 *         Date: 03/05/2016
 *         Time: 00:16
 */
public class SystemEventHandler {

    private ThumbMaker thumbMaker;
    private Configuration configuration;

    public SystemEventHandler(Configuration configuration) {
        this.configuration = configuration;
        this.thumbMaker = new ThumbMaker(configuration.DCRAW_COMMAND);
    }

    public void handleSystemChangeEvent(Path file, WatchEvent.Kind kind) {
        if (kind.equals(ENTRY_CREATE)) {
            entryCreate(file);
        } else if (kind.equals(ENTRY_DELETE)) {

        }
    }

    private void entryCreate(Path file) {
        if (isFileRaw(file)) {
            System.out.format("File %s is Raw Photo file. Converting", file);
            Path thumb = convert(file);
            System.out.printf("Thumb %s is created", thumb);
        }
    }

    private static String getFileExtension(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

        if (i > p) {
            extension = fileName.substring(i + 1);
        }
        return extension;
    }

    

    private Path convert(Path rawPhotoFile) {
        String original = rawPhotoFile.toString();
        String thumb = original.replace(configuration.BASE_FOLDER, configuration.THUMB_FOLDER)
                .replace(configuration.RAW_EXTENSION, configuration.THUMB_EXTENSION)
                .replace(configuration.RAW_EXTENSION.toLowerCase(), configuration.THUMB_EXTENSION);
        thumbMaker.createThumb(original, thumb);
        return Paths.get(thumb);
    }

    private boolean isFileRaw(Path file) {
        return (getFileExtension(file.toString()).equalsIgnoreCase(configuration.RAW_EXTENSION));
    }
}
