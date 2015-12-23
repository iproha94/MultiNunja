package com.wordpress.ilyaps.services.gamemechService;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ilya on 27.10.15.
 */
public class GameSession {
    private final long startTime;
    @NotNull
    private final Map<String, GameUser> users = new ConcurrentHashMap<>();

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

    public void removeGameUser(String userName) {
        users.remove(userName);
    }
}
