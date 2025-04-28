package com.example;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class Config {
    private static Configuration config;
    private static String env;

    static {
        env = System.getProperty("env", "local");
        Configurations configs = new Configurations();
        try {
            config = configs.properties(
                    Config.class.getClassLoader().getResource("application-" + env + ".properties"));
        } catch (ConfigurationException | NullPointerException e) {
            throw new RuntimeException("Failed to load configuration for env: " + env, e);
        }
    }

    public static int getPort() {
        return config.getInt("port", 8080);
    }

    public static String getEnv() {
        return env;
    }
}
