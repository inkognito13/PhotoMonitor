package su.tarasov.watchdir;

import java.io.*;

/**
 * @author Dmitry Tarasov
 *         Date: 02/23/2016
 *         Time: 14:31
 */
public class ThumbMaker {

    private String dcrawPath;

    public ThumbMaker(String dcrawPath) {
        this.dcrawPath = dcrawPath;
    }

    public boolean createThumb(String originalFileName, String thumbFileName) {
        try {
            checkFolderAndCreate(thumbFileName);
            return callDcraw(originalFileName, thumbFileName);
        }catch (Exception e){
            System.out.format("Error creating thumb with orig %s and thumb %s", originalFileName, thumbFileName);
            e.printStackTrace();
            return false;
        }
    }

    private boolean callDcraw(String originalFileName, String thumbFileName) {
            boolean success = false;
            try {
                String[] args = {dcrawPath,"-e", "-c",originalFileName};
                Process process = new ProcessBuilder(args).start();
                System.out.println("Executing command "+dcrawPath+" -e -c " + originalFileName+"'");

                InputStream processOutput = new BufferedInputStream(process.getInputStream());
                OutputStream fileOut = new FileOutputStream(new File(thumbFileName));

                int cnt;
                byte[] buffer = new byte[1024];
                while ( (cnt = processOutput.read(buffer)) != -1) {
                    fileOut.write(buffer, 0, cnt );
                }

                BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String line = null;
                while ((line = stderr.readLine()) != null){
                    System.out.println(line);
                }

                if (process.waitFor() == 0) {
                    System.out.println("Success!");
                    success = true;
                }
            }catch (Exception e){
                System.out.format("Error executing command '%s'",dcrawPath+" -e -c " + originalFileName);
                e.printStackTrace();
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
