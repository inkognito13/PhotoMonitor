package su.tarasov.watchdir;

import junit.framework.Assert;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;

import static org.junit.Assert.*;

/**
 * @author Dmitry Tarasov
 *         Date: 02/24/2016
 *         Time: 17:54
 */

public class ThumbTest {

    private static final String DCRAW_COMMAND = "dcraw";
    private static final String RAW_FILE_SUBFOLDER = "/raw/test/DSC_3771.NEF";
    private static final String THUMB_FILE_SUBFOLDER = "/thumb/test/DSC_3771.jpg";

    private File getResourceFile(String fileName) throws URISyntaxException{
        return new File(getClass().getResource(fileName).getFile());
    }

    @Test
    public void subFolderCreateTest() throws Exception{
        File rawFile = getResourceFile(RAW_FILE_SUBFOLDER);
        String thumbFileName = rawFile.getAbsolutePath().replace("raw","thumb").replace("NEF","jpg");
        File thumbFile = new File(thumbFileName);

        if (thumbFile.exists()){
            thumbFile.delete();
        }

        File thumbDir = thumbFile.getParentFile();

        if (thumbDir.exists()){
            thumbDir.delete();
        }

        ThumbMaker maker = new ThumbMaker(DCRAW_COMMAND);
        assertTrue(maker.createThumb(rawFile.getAbsolutePath(),thumbFileName));
        assertTrue(getResourceFile(THUMB_FILE_SUBFOLDER).exists());
    }

}
