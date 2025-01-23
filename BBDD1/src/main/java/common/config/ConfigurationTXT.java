package common.config;
import jakarta.inject.Singleton;

import java.io.IOException;
import java.util.Properties;
import common.constants.CommonConstants;
@Singleton
public class ConfigurationTXT {

    private static ConfigurationTXT instance=null;
    private Properties p;
    private ConfigurationTXT() {
        try {
            p = new Properties();
            p.load(ConfigurationTXT.class.getClassLoader().getResourceAsStream(CommonConstants.CONFIG_FILE_NAME_TXT));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ConfigurationTXT getInstance() {
        if (instance==null) {
            instance=new ConfigurationTXT();
        }
        return instance;
    }

    public String getProperty(String key) {
        return p.getProperty(key);
    }
}
