package com.wordpress.ilyaps.databaseHelpers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by ilyap on 23.11.2015.
 */
public class DBExecutor {
    @SuppressWarnings("ConstantConditions")
    @NotNull
    static final Logger LOGGER = LogManager.getLogger(DBExecutor.class);

    @Nullable
    public static <T> T execQuery(@NotNull Connection connection,
                            @NotNull String query,
                            @NotNull TResultHandler<T> handler)
            throws SQLException
    {
        T value;

        try (Statement stmt = connection.createStatement();
             ResultSet result = stmt.executeQuery(query)) {
                value = handler.handle(result);
            }
        return value;
    }

    public static int execUpdate(@NotNull Connection connection,
                          @NotNull String update)
            throws SQLException
    {
        int count = 0;
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(update);
            count = stmt.getUpdateCount();
        }
        return count;
    }

}