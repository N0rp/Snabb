package eu.dowsing.leap.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class MainProperties {

    private static final MainProperties instance = new MainProperties();

    public enum Key {
        MUSIC_LOCATION, BEEP_SOUND
    };

    private static final String PROPERTIES_LOCATION = "config.properties";

    private Properties prop = new Properties();

    private MainProperties() {
        loadProperties();
    };

    /** Initialize the default properties. Useful to clear properties data and set the default. **/
    private void initDefaultProperties(Properties p) {
        // set all default properties
        p.setProperty(Key.MUSIC_LOCATION.toString(), "");
        p.setProperty(Key.BEEP_SOUND.toString(), "");
    }

    public static MainProperties getInstance() {
        return instance;
    }

    /**
     * Get the a property
     * 
     * @return
     */
    public String getProperty(Key key) {
        return this.prop.getProperty(key.toString());
    }

    /**
     * Set the a property
     * 
     * @return
     */
    public void setProperty(Key key, String value) {
        this.prop.setProperty(key.toString(), value);
    }

    /**
     * Save the current properties
     */
    public void saveProperties() {
        try {
            // save properties
            prop.store(new FileOutputStream(PROPERTIES_LOCATION), null);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Load the current properties
     */
    public void loadProperties() {
        File file = new File(PROPERTIES_LOCATION);

        if (!file.exists()) {

            try {
                // set the default properties values
                initDefaultProperties(prop);

                // save properties
                prop.store(new FileOutputStream(PROPERTIES_LOCATION), null);

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        try {
            // load a properties file
            prop.load(new FileInputStream(PROPERTIES_LOCATION));
            System.out.println("Properties has: " + prop.getProperty(Key.MUSIC_LOCATION.toString()));

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
