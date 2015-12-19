package com.wordpress.ilyaps.databaseHelpers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by ilyap on 23.11.2015.
 */
public class DBConfiguration {
    @NotNull
    static final Logger LOGGER = LogManager.getLogger(DBConfiguration.class);

    @NotNull
    private String nameDriver;
    @NotNull
    private String jdbcUrl;
    @NotNull
    private String userName;
    @NotNull
    private String password;

    public DBConfiguration(@NotNull String fileName) {
        String readedJdbcUrl;
        String readedNameDriver;
        String readedUserName;
        String readedPassword;

        try (final FileInputStream fis = new FileInputStream(fileName)) {
            final Properties properties = new Properties();
            properties.load(fis);
            readedJdbcUrl = properties.getProperty("jdbc_url");
            readedNameDriver = properties.getProperty("name_driver");
            readedUserName = properties.getProperty("user_name");
            readedPassword = properties.getProperty("user_password");
        } catch (IOException ignored) {
            LOGGER.error(ignored);
            throw new RuntimeException();
        }

        if (readedJdbcUrl == null) {
            LOGGER.error("JdbcUrl == null");
            throw new NullPointerException("jdbcUrl");
        }
        jdbcUrl = readedJdbcUrl;

        if (readedNameDriver == null) {
            LOGGER.error("NameDriver == null");
            throw new NullPointerException("nameDriver");
        }
        nameDriver = readedNameDriver;

        if (readedUserName == null) {
            LOGGER.error("UserName == null");
            throw new NullPointerException("userName");
        }
        userName = readedUserName;

        if (readedPassword == null) {
            LOGGER.error("Password == null");
            throw new NullPointerException("password");
        }
        password = readedPassword;
    }

    @NotNull
    public String getNameDriver() {
        return nameDriver;
    }

    @NotNull
    public String getJdbcUrl() {
        return jdbcUrl;
    }

    @NotNull
    public String getUserName() {
        return userName;
    }

    @NotNull
    public String getPassword() {
        return password;
    }
}
