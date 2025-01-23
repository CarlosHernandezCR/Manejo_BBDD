package common.config;

import common.constants.CommonConstants;

import java.io.IOException;
import java.util.Properties;

public class ConfigurationXML {

    private static ConfigurationXML instance = null;
    private Properties p;

    private ConfigurationXML() {
        try {
            p = new Properties();
            p.loadFromXML(ConfigurationXML.class.getClassLoader().getResourceAsStream(CommonConstants.CONFIG_FILE_NAME_XML));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ConfigurationXML getInstance() {
        if (instance == null) {
            instance = new ConfigurationXML();
        }
        return instance;
    }

    public String getProperty(String key) {
        return p.getProperty(key);
    }
}