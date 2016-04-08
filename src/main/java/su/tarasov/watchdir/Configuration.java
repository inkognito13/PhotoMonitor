package su.tarasov.watchdir;

/**
 * @author Dmitry Tarasov
 *         Date: 02/23/2016
 *         Time: 14:59
 */
public class Configuration {
    public String DCRAW_COMMAND = "/usr/local/Cellar/dcraw/9.26.0/bin/dcraw";
    public String ACDCLI_COMMAND = "acd_cli";
    public String BASE_FOLDER = "/mnt/Pool/documents/photos";
    public String THUMB_FOLDER = "/mnt/Pool/documents/thumbs";
    public String RAW_EXTENSION = "NEF";
    public String THUMB_EXTENSION = "jpg";
    public String PHOTOS_REMOTE_FOLDER = "/Pictures/NAS/photos";
    public String RAW_REMOTE_FOLDER = "/Pictures/NAS/backup";
    public boolean recursive = false;

    public Configuration() {
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Configuration{\n");
        sb.append("                DCRAW_COMMAND='").append(DCRAW_COMMAND).append("'\n");
        sb.append(",                 ACDCLI_COMMAND='").append(ACDCLI_COMMAND).append("'\n");
        sb.append(",                 BASE_FOLDER='").append(BASE_FOLDER).append("'\n");
        sb.append(",                 THUMB_FOLDER='").append(THUMB_FOLDER).append("'\n");
        sb.append(",                 RAW_EXTENSION='").append(RAW_EXTENSION).append("'\n");
        sb.append(",                 THUMB_EXTENSION='").append(THUMB_EXTENSION).append("'\n");
        sb.append(",                 PHOTOS_REMOTE_FOLDER='").append(PHOTOS_REMOTE_FOLDER).append("'\n");
        sb.append(",                 RAW_REMOTE_FOLDER='").append(RAW_REMOTE_FOLDER).append("'\n");
        sb.append(",                 recursive=").append(recursive);
        sb.append("\n}");
        return sb.toString();
    }
}
