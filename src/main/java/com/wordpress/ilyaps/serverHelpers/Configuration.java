package com.wordpress.ilyaps.serverHelpers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Created by stalker on 01.11.15.
 */
public final class Configuration {
    @NotNull
    static final Logger LOGGER = LogManager.getLogger(Configuration.class);
    @NotNull
    private final Properties properties = new Properties();
    @NotNull
    private final List<String> listImportantProperies = new ArrayList<>(Arrays.asList(
            "host",
            "port",
            "gameSocketPort",
            "gameSocketHost",
            "gameSocketUrl",
            "signinPageUrl",
            "signupPageUrl",
            "logoutPageUrl",
            "mainPageUrl",
            "scoresPageUrl",
            "adminPageUrl",
            "resourcesDirectory")
    );

    public Configuration(@NotNull String propertiesFile) {

        try (final FileInputStream fis = new FileInputStream(propertiesFile)) {
            properties.load(fis);
        } catch (IOException ignored) {
            LOGGER.error("IOException read the file of properties");
            throw new RuntimeException();
        }

        for (String property: listImportantProperies) {
            String value = properties.getProperty(property);

            if (value == null) {
                LOGGER.error("пустое свойство " + property);
                throw new NullPointerException();
            }
            LOGGER.info(property + "  = " + value);
        }
    }

    @NotNull
    public String getValueOfProperty(@NotNull String property) {
        String value = properties.getProperty(property);
        if (value == null) {
            LOGGER.error(property + " == null");
            throw new NullPointerException();
        }
        return value;
    }
}
