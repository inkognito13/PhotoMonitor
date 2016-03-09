package su.tarasov.watchdir;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author Dmitry Tarasov
 *         Date: 02/24/2016
 *         Time: 14:01
 */
public class App {

    private static Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws IOException {

        Options options = new Options();
        options.addOption("b", "base-folder", true, "Base folder to watch");
        options.addOption("t", "thumb-folder", true, "Folder for thumbs");
        options.addOption("r", "recursive", false, "Watch recursive");
        options.addOption("d","dcraw-command",true,"Dcraw command");
        options.addOption("a","acd-command",true,"ACD command");
        options.addOption("bm", "batch-mode", false, "Batch mode");
        

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            Configuration configuration = new Configuration();
            configuration.BASE_FOLDER = cmd.getOptionValue("b");
            configuration.THUMB_FOLDER = cmd.getOptionValue("t");
            configuration.DCRAW_COMMAND = cmd.getOptionValue("d");
            configuration.ACDCLI_COMMAND = cmd.getOptionValue("a");
            configuration.recursive = cmd.hasOption("r");
            boolean batchMode = cmd.hasOption("bm");
            logger.debug("Starting with conf {}", configuration);
            if (batchMode) {
                logger.debug("Running in BATCH mode");
                new BatchManager(configuration).runCompleteBatch();
            } else {
                logger.debug("Running in MONITOR mode");
                new MonitorService(configuration).processEvents();
            }

        } catch (ParseException e) {
            logger.error("Parsing arguments failed ", e);
        }
    }

}
