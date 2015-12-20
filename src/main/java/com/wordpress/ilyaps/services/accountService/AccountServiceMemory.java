package com.wordpress.ilyaps.services.accountService;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wordpress.ilyaps.ThreadSettings;
import com.wordpress.ilyaps.messageSystem.Address;
import com.wordpress.ilyaps.messageSystem.Message;
import com.wordpress.ilyaps.messageSystem.MessageSystem;
import com.wordpress.ilyaps.serverHelpers.GameContext;
import com.wordpress.ilyaps.services.accountService.dataset.Score;
import com.wordpress.ilyaps.services.accountService.dataset.UserProfile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ilya on 12.12.15.
 */
public class AccountServiceMemory implements AccountService {
    @NotNull
    static final Logger LOGGER = LogManager.getLogger(AccountServiceMemory.class);
    @NotNull
    private final Address address = new Address();
    @NotNull
    private final MessageSystem messageSystem;
    @NotNull
    private final Map<String, UserProfile> users = new HashMap<>();
    @NotNull
    private final Map<String, UserProfile> sessions = new HashMap<>();
    @NotNull
    private final List<Score> listScores = new ArrayList<>();

    public AccountServiceMemory() {
        GameContext gameContext = GameContext.getInstance();

        this.messageSystem = (MessageSystem) gameContext.get(MessageSystem.class);
        messageSystem.addService(this);
        messageSystem.getAddressService().registerAccountService(this);

        register("Egor","test1@mail.ru","1234");
        register("Ilya","test2@mail.ru","1234");
        register("Jenya","test3@mail.ru","1234");
        register("Dmitriy","test4@mail.ru","1234");
        register("Konstantin","test5@mail.ru","1234");
    }

    @NotNull
    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public void sendMessage(Message msg) {
        messageSystem.sendMessage(msg);
    }

    @Override
    public void run() {
        LOGGER.info("start thread");

        while (true) {
            messageSystem.execForAbonent(this);
            try {
                Thread.sleep(ThreadSettings.SLEEP_TIME);
            } catch (InterruptedException e) {
                LOGGER.error("sleep thread");
                LOGGER.error(e);
            }
        }
    }

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

    @Override
    public void removeAllSessions() {
        sessions.clear();
    }
}
