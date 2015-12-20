package com.wordpress.ilyaps.services.gamemechService;

import com.wordpress.ilyaps.messageSystem.Abonent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

/**
 * Created by ilya on 13.12.15.
 */
public interface GamemechService extends Abonent, Runnable {
    void receiveData(@NotNull String name, @NotNull String data);
    void sendData(@NotNull String name, @NotNull String data);
    void addUser(@NotNull String name);
    void removeUser(@NotNull String name);
    @NotNull Map<String, GameSession> getNameToGame();
    @NotNull Set<GameSession> getAllSessions();
}
