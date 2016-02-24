package su.tarasov.watchdir;

import org.apache.commons.cli.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Dmitry Tarasov
 *         Date: 02/24/2016
 *         Time: 14:01
 */
public class App {
    public static void main(String[] args) throws IOException {

        Options options = new Options();
        options.addOption("b", "base-folder", true, "Base folder to watch");
        options.addOption("t", "thumb-folder", true, "Folder for thumbs");
        options.addOption("r", "recursive", false, "Watch recursive");

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            Configuration configuration = new Configuration();
            configuration.BASE_FOLDER = cmd.getOptionValue("b");
            configuration.THUMB_FOLDER = cmd.getOptionValue("t");
            configuration.recursive = cmd.hasOption("r");
            System.out.println("Starting with conf "+configuration);
            new WatchDir(configuration).processEvents();

        } catch (ParseException e) {
            System.out.println("Parsing arguments failed " + e.getMessage());
        }
    }

}
