package com.wordpress.ilyaps.databaseHelpers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by ilyap on 23.11.2015.
 */
@FunctionalInterface
public interface TResultHandler<T> {
    @Nullable T handle(@NotNull ResultSet resultSet) throws SQLException;
}
