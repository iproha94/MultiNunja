package com.wordpress.ilyaps.services.accountService;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wordpress.ilyaps.databaseHelpers.DBService;
import com.wordpress.ilyaps.services.accountService.dao.ScoreDAODB;
import com.wordpress.ilyaps.services.accountService.dao.UserDAODB;
import com.wordpress.ilyaps.services.accountService.dataset.Score;
import com.wordpress.ilyaps.services.accountService.dataset.UserProfile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ilya on 19.12.15.
 */
public class AccountServiceDAODB implements AccountServiceDAO {
    @NotNull
    static final Logger LOGGER = LogManager.getLogger(AccountServiceDAODB.class);
    @NotNull
    private final DBService dbService;
    @NotNull
    private final Map<String, UserProfile> sessions = new HashMap<>();

    public AccountServiceDAODB(@NotNull String configurationFileName) {
        dbService = new DBService(configurationFileName);
    }

    @Nullable
    @Override
    public UserProfile register(@NotNull String name, @NotNull String email, @NotNull String password) {
        int count = 0;
        UserProfile user = new UserProfile(name, email, password);
        try (Connection con = dbService.openConnection()) {
            UserDAODB userdao = new UserDAODB(con);
            count = userdao.insert(user);
        } catch (SQLException ignored) {
            LOGGER.warn("userdao.insert");
            LOGGER.warn(ignored);
        }

        if (count == 0) {
            return null;
        } else {
            return user;
        }
    }

    @Nullable
    @Override
    public UserProfile authorization(@NotNull String sessionId, @NotNull String email, @NotNull String password) {
        UserProfile profile = getRegisteredUser(email);
        if (profile == null || !password.equals(profile.getPassword())) {
            return null;
        }

        sessions.put(sessionId, profile);
        return profile;
    }

    @Nullable
    @Override
    public UserProfile leaving(@NotNull String sessionId) {
        return sessions.remove(sessionId);
    }

    @Nullable
    @Override
    public UserProfile getRegisteredUser(@NotNull String email) {
        UserProfile user = null;

        try (Connection con = dbService.openConnection()) {
            UserDAODB userdao = new UserDAODB(con);
            user = userdao.readByEmail(email);
        } catch (SQLException ignored) {
            LOGGER.warn("userdao.insert");
            LOGGER.warn(ignored);
        }

        return user;
    }

    @Nullable
    @Override
    public UserProfile getAuthorizedUser(@NotNull String sessionId) {
        return sessions.get(sessionId);
    }

    @Override
    public void addScore(@NotNull String name, int score) {
        Score objscore = new Score(name, score);
        try (Connection con = dbService.openConnection()) {
            ScoreDAODB scoredao = new ScoreDAODB(con);
            scoredao.insert(objscore);
        } catch (SQLException ignored) {
            LOGGER.warn("scoredao.insert");
            LOGGER.warn(ignored);
        }
    }

    @NotNull
    @Override
    public String getScore(int start, int amount) {
        JsonObject result = new JsonObject();
        result.addProperty("status", 200);

        JsonArray arr = new JsonArray();

        try (Connection con = dbService.openConnection()) {
            ScoreDAODB scoredao = new ScoreDAODB(con);

            for (Score score : scoredao.read(start, amount)) {
                JsonObject guser = new JsonObject();
                guser.addProperty("name", score.getName());
                guser.addProperty("score", score.getScore());
                arr.add(guser);
            }

        } catch (SQLException ignored) {
            LOGGER.warn("scoredao.insert");
            LOGGER.warn(ignored);
        }

        result.add("players", arr);

        return result.toString();
    }

    @Override
    public int countRegisteredUser() {
        int count = 0;

        try (Connection con = dbService.openConnection()) {
            UserDAODB userdao = new UserDAODB(con);
            count = userdao.count();
        } catch (SQLException ignored) {
            LOGGER.warn("userdao.count");
            LOGGER.warn(ignored);
        }

        return count;
    }

    @Override
    public int countAuthorizedUser() {
        return sessions.size();
    }
}
