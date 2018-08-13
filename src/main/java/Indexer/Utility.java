package Indexer;

import Indexer.GUI.Settings;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.net.URISyntaxException;

/**
 * Created by Richard on 2018-08-14.
 */
public class Utility {
    private static final String OUTPUT_DIRECTORY = "/../out/config/";

    public static String getOutputDirectory() {
        String fullPath = null;
        try {
            fullPath = new File(Settings.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()
                    .getPath()).getParent();
            fullPath += OUTPUT_DIRECTORY;
            if (SystemUtils.IS_OS_WINDOWS) {
                fullPath = fullPath.replaceFirst("/", "//");
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return fullPath;
    }
}
