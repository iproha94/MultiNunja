package com.wordpress.ilyaps.services.accountService.dao;

import com.wordpress.ilyaps.databaseHelpers.DBExecutor;
import com.wordpress.ilyaps.services.accountService.dataset.UserProfile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by ilya on 29.11.15.
 */
public class UserDAODB {
    @NotNull
    static final Logger LOGGER = LogManager.getLogger(UserDAODB.class);
    @NotNull
    private final Connection connection;

    public UserDAODB(@NotNull Connection connection) {
        this.connection = connection;
    }

    public int insert(@NotNull UserProfile user) throws SQLException {
        String query = "insert into user (name, password, email) " +
                "values ( '" + user.getName() + "' , '"
                + user.getPassword() + "' , "
                + " '" + user.getEmail() + "' )";
        return DBExecutor.execUpdate(connection, query);
    }

    @Nullable
    public UserProfile readByEmail(String email) throws SQLException {
        String query = "select * from user where email = '" + email + "' ";

        return DBExecutor.execQuery(connection,
            query,
            result -> {
                UserProfile newuser = null;
                if (result.next()) {
                    String uname = result.getString("name");
                    String upassword = result.getString("password");
                    String uemail = result.getString("email");

                    newuser = new UserProfile(uname, uemail, upassword);
                }

                return newuser;
            });
    }

    public int count() throws SQLException {
        String query = "SELECT count(*) FROM user";

        return DBExecutor.execQuery(connection, query,
                result -> {
                    if (result.next()) {
                        return result.getInt(1);
                    } else {
                        LOGGER.warn("count score: result == null");
                        return 0;
                    }
                });
    }

    public int deleteAll() throws SQLException {
        return DBExecutor.execUpdate(connection, "delete from user ");
    }
}
