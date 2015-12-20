package com.wordpress.ilyaps.services.socketsService;

import com.wordpress.ilyaps.frontendSockets.GameWebSocket;
import com.wordpress.ilyaps.messageSystem.Abonent;
import org.jetbrains.annotations.NotNull;

/**
 * Created by ilya on 28.11.15.
 */
public interface SocketsService extends Abonent, Runnable {
    void add(@NotNull GameWebSocket userSocket);

    void remove(@NotNull String name);


    void openSocket(@NotNull String name);

    void closeSocket(@NotNull String name);


    void receiveData(@NotNull String name, @NotNull String data);

    void sendData(@NotNull String name, @NotNull String data);
}
