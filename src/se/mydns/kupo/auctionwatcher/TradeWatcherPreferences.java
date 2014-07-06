package se.mydns.kupo.auctionwatcher;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Properties;

/**
 * user preferences class. read and write to properties file and make prefences availble.
 */
public class TradeWatcherPreferences {
    String slash = FileSystems.getDefault().getSeparator();
    private String preferencesFile = "." + slash + "res" + slash + "tradewatcher.properties";
    private Properties properties = new Properties();
    File f = new File(preferencesFile);

    public TradeWatcherPreferences() {
        readPreferences();
    }

    private void readPreferences() {
        if(!Files.isRegularFile(f.toPath())) {
            // properties file does not exist. not preferences to read. Set some defaults.
            properties.setProperty("notification.popup", "true");
            properties.setProperty("notification.audio", "false");
        } else {
            try {
                properties.load(new FileInputStream(f));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        for(String key : properties.stringPropertyNames()) {
//            String value = properties.getProperty(key);
//            System.out.println(key + " => " + value);
//        }
    }

    public Properties getPrefs() {
        return properties;
    }

    public void writePreferences() {
        try {
            properties.store(new FileWriter(f), null);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void setNotificationPopup(boolean notificationPopup) {
        if(notificationPopup)
            properties.setProperty("notification.popup", "true");
        else
            properties.setProperty("notification.popup", "false");

        writePreferences();
    }
}
