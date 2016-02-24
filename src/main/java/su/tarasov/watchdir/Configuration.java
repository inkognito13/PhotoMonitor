package su.tarasov.watchdir;

/**
 * @author Dmitry Tarasov
 *         Date: 02/23/2016
 *         Time: 14:59
 */
public class Configuration {
    public String DCRAW_COMMAND = "/usr/local/Cellar/dcraw/9.26.0/bin/dcraw";
    public String BASE_FOLDER = "/mnt/Pool/documents/photos";
    public String THUMB_FOLDER = "/mnt/Pool/documents/thumbs";
    public String RAW_EXTENSION = "NEF";
    public String THUMB_EXTENSION = "jpg";
    public boolean recursive = false;

    public Configuration() {
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "DCRAW_COMMAND='" + DCRAW_COMMAND + '\'' +
                ", BASE_FOLDER='" + BASE_FOLDER + '\'' +
                ", THUMB_FOLDER='" + THUMB_FOLDER + '\'' +
                ", RAW_EXTENSION='" + RAW_EXTENSION + '\'' +
                ", THUMB_EXTENSION='" + THUMB_EXTENSION + '\'' +
                ", recursive=" + recursive +
                '}';
    }
}
