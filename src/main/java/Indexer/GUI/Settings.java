package Indexer.GUI;

import java.io.*;
import java.net.URISyntaxException;
import java.util.Properties;

import static Indexer.Utility.getOutputDirectory;

public class Settings {
    public static final String RELATIVE_RESOURCE_PATH = "src/main/resources/";
    private static final String SETTINGS_FILE = "settings.properties";

    private static Properties defaultProps = new Properties();
    static {
        try {
//            InputStream in = Thread.currentThread()
//                    .getContextClassLoader()
//                    .getResourceAsStream(filename);
            InputStream in = new FileInputStream(getOutputDirectory() + SETTINGS_FILE);
            defaultProps.load(in);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void updateProperty(String key, String newVal) {
        defaultProps.setProperty(key, newVal);
    }

    static void storeProperties(){
        try {
            FileOutputStream out = new FileOutputStream(getOutputDirectory() + SETTINGS_FILE);
            defaultProps.store(out, null);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static String getStringProperty(String key) {
        return defaultProps.getProperty(key);
    }
    static Integer getIntegerProperty(String key) {
        return Integer.parseInt(defaultProps.getProperty(key));
    }
    static Double getDoubleProperty(String key) {
        return Double.parseDouble(defaultProps.getProperty(key));
    }
}
