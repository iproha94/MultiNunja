package com.wordpress.ilyaps.databaseHelpers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by ilyap on 23.11.2015.
 */
public class DBService {
    @NotNull
    static final Logger LOGGER = LogManager.getLogger(DBService.class);
    @NotNull
    private final String nameDriver;
    @NotNull
    private final String jdbcUrl;
    @NotNull
    private final String userName;
    @NotNull
    private final String password;

    public DBService(@NotNull String configurationFileName) {
        DBConfiguration dbConfiguration = new DBConfiguration(configurationFileName);

        this.nameDriver = dbConfiguration.getNameDriver();
        this.jdbcUrl = dbConfiguration.getJdbcUrl();
        this.userName = dbConfiguration.getUserName();
        this.password = dbConfiguration.getPassword();

        try {
            Driver driver = (Driver) Class.forName(nameDriver).newInstance();
            DriverManager.registerDriver(driver);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException ignored) {
            LOGGER.error(ignored);
            throw new RuntimeException();
        }

        try (Connection con = openConnection()) {
            loggerInfoOfConnection(con);
        } catch (SQLException ignored) {
            LOGGER.warn(ignored);
        }
    }

    @NotNull
    public Connection openConnection() {
        Connection con = null;
        try {
            con = DriverManager.getConnection(jdbcUrl, userName, password);
        } catch (SQLException ignored) {
            LOGGER.error("open connection");
            LOGGER.error(ignored);
        }

        if (con == null) {
            LOGGER.error("connection == null");
            throw new NullPointerException();
        }
        return con;
    }

    public void loggerInfoOfConnection(@Nullable Connection connection) {
        if (connection == null) {
            LOGGER.info("connection == null");
            return;
        }

        try {
            LOGGER.info("Autocommit: " + connection.getAutoCommit());
            LOGGER.info("DB name: " + connection.getMetaData().getDatabaseProductName());
            LOGGER.info("DB version: " + connection.getMetaData().getDatabaseProductVersion());
            LOGGER.info("Driver name: " + connection.getMetaData().getDriverName());
            LOGGER.info("Driver version: " + connection.getMetaData().getDriverVersion());
        } catch (SQLException e) {
            LOGGER.error(e);
        }
    }
}
