package su.tarasov.watchdir;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @author Dmitry Tarasov
 *         Date: 02/23/2016
 *         Time: 14:31
 */
public class ThumbMaker {

    private String dcrawPath;

    private Logger logger = LoggerFactory.getLogger(ThumbMaker.class);

    public ThumbMaker(String dcrawPath) {
        this.dcrawPath = dcrawPath;
    }

    public boolean createThumb(String originalFileName, String thumbFileName) {
        try {
            checkFolderAndCreate(thumbFileName);
            return callDcraw(originalFileName, thumbFileName);
        }catch (Exception e){
            logger.error("Error creating thumb with orig {} and thumb {}", originalFileName, thumbFileName);
            logger.error("Error ", e);
            return false;
        }
    }

    private boolean callDcraw(String originalFileName, String thumbFileName) {
            boolean success = false;
            try {
                String[] args = {dcrawPath,"-e", "-c",originalFileName};
                Process process = new ProcessBuilder(args).start();
                logger.debug("Executing command " + dcrawPath + " -e -c " + originalFileName + "'");

                InputStream processOutput = new BufferedInputStream(process.getInputStream());
                OutputStream fileOut = new FileOutputStream(new File(thumbFileName));

                int cnt;
                byte[] buffer = new byte[1024];
                while ( (cnt = processOutput.read(buffer)) != -1) {
                    fileOut.write(buffer, 0, cnt );
                }

                BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                StringBuilder builder = new StringBuilder();
                String line = null;

                while ((line = stderr.readLine()) != null){
                    builder.append(line);
                }

                if (builder.length() > 0) {
                    logger.error("Error executing dcraw command {}", builder.toString());
                }

                if (process.waitFor() == 0) {
                    logger.debug("dcraw command Success!");
                    success = true;
                }
            }catch (Exception e){
                logger.error("Error executing command '%s'", dcrawPath + " -e -c " + originalFileName, e);
            }

            return success;
    }

    private void checkFolderAndCreate(String fileToCreate) throws IOException {
        File parentFolder = (new File(fileToCreate)).getParentFile();
        if (!parentFolder.exists()) {
            parentFolder.mkdirs();
        }
    }
}
