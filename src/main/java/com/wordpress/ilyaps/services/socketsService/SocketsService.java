package com.wordpress.ilyaps.services.socketsService;

import com.wordpress.ilyaps.frontendSockets.GameWebSocket;
import com.wordpress.ilyaps.messageSystem.Abonent;
import org.jetbrains.annotations.NotNull;

/**
 * Created by ilya on 28.11.15.
 */
public interface SocketsService extends Abonent, Runnable {
    void add(@NotNull GameWebSocket userSocket);

    boolean remove(@NotNull String name);

    void notify(@NotNull String userName, @NotNull String message);

    void openSocket(String name);

    void closeSocket(String name);


    void receiveData(String name, String data);

    void sendData(String name, String data);
}
