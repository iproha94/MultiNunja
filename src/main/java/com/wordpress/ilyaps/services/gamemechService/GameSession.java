package com.wordpress.ilyaps.services.gamemechService;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Created by ilya on 27.10.15.
 */
public class GameSession {
    private final long startTime;
    @NotNull
    private final Map<String, GameUser> users = new HashMap<>();

    public GameSession(@NotNull Set<String> namesPlayers) {
        startTime = new Date().getTime();

        for (String nameUser : namesPlayers) {
            GameUser gameUser = new GameUser(nameUser);
            users.put(nameUser, gameUser);
        }
    }

    public long getSessionTime() {
        return new Date().getTime() - startTime;
    }

    @Nullable
    public GameUser getGameUser(@NotNull String nameUser) {
        return users.get(nameUser);
    }

    @NotNull
    public Collection<GameUser> getGameUsers() {
        return users.values();
    }

    @NotNull
    public String getNameWinner() {
        String nameWinner = "";
        int maxScore = 0;
        for (GameUser gameUser : users.values()) {
            if (gameUser.getScore() >= maxScore) {
                maxScore = gameUser.getScore();
                nameWinner = gameUser.getName();
            }
        }
        return nameWinner;
    }

    public boolean removeGameUser(String userName) {
        GameUser gameUser = users.get(userName);

        if (gameUser == null) {
            return false;
        }
        users.remove(userName);
        return true;
    }
}
