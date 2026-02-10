package kz.aitu.music_library_api.patterns;

import org.springframework.stereotype.Component;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

@Component
public class AppConfig {

    private static AppConfig instance;
    private Properties properties;
    private final String appName;
    private final String version;
    private final int maxPlaylistSize;

    private AppConfig() {
        this.properties = new Properties();
        this.appName = "Music Library API";
        this.version = "2.0";
        this.maxPlaylistSize = 1000;
        loadProperties();
    }

    public static AppConfig getInstance() {
        if (instance == null) {
            synchronized (AppConfig.class) {
                if (instance == null) {
                    instance = new AppConfig();
                }
            }
        }
        return instance;
    }

    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            System.err.println("Could not load application.properties: " + e.getMessage());
        }
    }

    public String getAppName() {
        return appName;
    }

    public String getVersion() {
        return version;
    }

    public int getMaxPlaylistSize() {
        return maxPlaylistSize;
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}