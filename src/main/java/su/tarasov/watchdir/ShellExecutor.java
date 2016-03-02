package su.tarasov.watchdir;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author Dmitry Tarasov
 *         Date: 02/23/2016
 *         Time: 14:34
 */
public class ShellExecutor {
    public static boolean executeCommand(String command){
        boolean success = false;
        try {
            Process process = Runtime.getRuntime().exec(command);
            System.out.println("Executing command "+command);
            BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line = null;
            while ((line = stdout.readLine()) != null){
                System.out.println(line);
            }

            while ((line = stderr.readLine()) != null){
                System.out.println(line);
            }

            if (process.waitFor() == 0) {
                System.out.println("Success!");
                success = true;
            }
        }catch (Exception e){
            System.out.format("Error executing command '%s'",command);
            e.printStackTrace();
        }

        return success;
    }
}
