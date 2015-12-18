package com.wordpress.ilyaps.gamemechService;

import com.wordpress.ilyaps.messageSystem.Abonent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

/**
 * Created by ilya on 13.12.15.
 */
public interface GamemechService extends Abonent, Runnable {
    void receiveData(String name, String data);
    void sendData(String name, String data);
    void addUser(@NotNull String name);
    boolean removeUser(@NotNull String name);
    Map<String, GameSession> getNameToGame();
    Set<GameSession> getAllSessions();
}
