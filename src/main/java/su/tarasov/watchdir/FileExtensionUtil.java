package su.tarasov.watchdir;

import java.nio.file.Path;

/**
 * @author Dmitry Tarasov
 *         Date: 04/08/2016
 *         Time: 21:38
 */
public class FileExtensionUtil {
    public static boolean isFileRaw(Path file, String rawExtension) {
        return (getFileExtension(file.toString()).equalsIgnoreCase(rawExtension));
    }

    public static String getFileExtension(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

        if (i > p) {
            extension = fileName.substring(i + 1);
        }
        return extension;
    }
}
