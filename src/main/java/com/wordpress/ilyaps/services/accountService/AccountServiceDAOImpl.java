package com.wordpress.ilyaps.services.accountService;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ilya on 12.12.15.
 */
public class AccountServiceDAOImpl implements AccountServiceDAO {
    @NotNull
    private final Map<String, UserProfile> users = new HashMap<>();
    @NotNull
    private final Map<String, UserProfile> sessions = new HashMap<>();
    @NotNull
    private final List<Score> listScores = new ArrayList<>();

    @Override
    @Nullable
    public UserProfile register(@NotNull String name, @NotNull String email, @NotNull String password) {
        synchronized (users) {
            if (users.containsKey(email)) {
                return null;
            }
            UserProfile profile = new UserProfile(name, email, password);
            users.put(email, profile);
            return profile;
        }
    }

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

    @Override
    public UserProfile getRegisteredUser(@NotNull String email) {
        return users.get(email);
    }

    @Nullable
    @Override
    public UserProfile getAuthorizedUser(@NotNull String sessionId) {
        return sessions.get(sessionId);
    }

    @Override
    public void addScore(@NotNull String name, int score) {
        listScores.add(new Score(name, score));
    }

    @NotNull
    @Override
    public String getScore(int start, int amount) {
        JsonObject result = new JsonObject();
        result.addProperty("status", 200);

        int i = 0;
        JsonArray arr = new JsonArray();
        for (Score score : listScores) {
            i++;
            JsonObject guser = new JsonObject();
            guser.addProperty("name", score.getName());
            guser.addProperty("score", score.getScore());
            arr.add(guser);
            if (i > amount) {
                break;
            }
        }
        result.add("players", arr);

        return result.toString();
    }

    @Override
    public int countRegisteredUser() {
        return users.size();
    }

    @Override
    public int countAuthorizedUser() {
        return sessions.size();
    }
}
