package su.tarasov.watchdir;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author Dmitry Tarasov
 *         Date: 02/23/2016
 *         Time: 14:34
 */
public class ShellExecutor {

    private Logger logger = LoggerFactory.getLogger(ShellExecutor.class);

    public boolean executeCommand(String command) {
        boolean success = false;
        try {
            Process process = Runtime.getRuntime().exec(command);
            logger.debug("Executing command {}", command);
            BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line = null;
            logger.debug("Command output {}", command);
            while ((line = stdout.readLine()) != null){
                logger.debug(line);
            }

            StringBuilder builder = new StringBuilder();
            line = null;

            while ((line = stderr.readLine()) != null){
                builder.append(line);
            }

            if (builder.length() > 0) {
                logger.error("Error executing command {}", builder.toString());
            }

            if (process.waitFor() == 0) {
                logger.debug("Success executing command {}", command);
                success = true;
            }
        }catch (Exception e){
            logger.error("Error executing command " + command, e);
        }

        return success;
    }
}
