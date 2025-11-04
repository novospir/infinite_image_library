package com.novospir.libraries.config;

import java.io.*;
import java.util.Properties;

public class ConfigLoader {
    private final Properties props = new Properties();
    private static final ConfigLoader INSTANCE = new ConfigLoader("config.properties");

    public ConfigLoader(String filename) {
        try (InputStream input = new FileInputStream(filename)) {
            props.load(input);
        } catch (IOException e) {
            System.err.println("Error loading config: " + e.getMessage());
        }
    }

    public static ConfigLoader getInstance(){
        return INSTANCE;
    }

    public String get(String key) {
        return props.getProperty(key);
    }

    public int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(props.getProperty(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public boolean getBool(String key, boolean defaultValue) {
        String val = props.getProperty(key);
        return val != null ? Boolean.parseBoolean(val) : defaultValue;
    }

    public void set(String key, String value) {
        props.setProperty(key, value);
    }

    public void save(String filename) {
        try (OutputStream output = new FileOutputStream(filename)) {
            props.store(output, "Updated configuration");
        } catch (IOException e) {
            System.err.println("Error saving config: " + e.getMessage());
        }
    }
}
