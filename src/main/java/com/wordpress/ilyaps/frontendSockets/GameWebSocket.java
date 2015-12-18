package com.wordpress.ilyaps.frontendSockets;

import com.wordpress.ilyaps.services.socketsService.SocketsService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Created by ilya on 27.10.15.
 */

@WebSocket
public class GameWebSocket {
    @NotNull
    static final Logger LOGGER = LogManager.getLogger(GameWebSocket.class);
    @NotNull
    private final String myName;
    @NotNull
    private final SocketsService sckService;
    private Session session;

    public GameWebSocket(@NotNull SocketsService sckService, @NotNull String myName) {
        this.myName = myName;
        this.sckService = sckService;
    }

    @NotNull
    public String getMyName() {
        return myName;
    }

    @OnWebSocketConnect
    public void onOpen(@NotNull Session newSession) {
        this.session = newSession;
        sckService.add(this);
        sckService.openSocket(myName);
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        sckService.closeSocket(myName);
        sckService.remove(myName);
    }

    @OnWebSocketMessage
    public void onMessage(String data) {
        if (data == null) {
            return;
        }
        sckService.receiveData(myName, data);
    }

    public void send(String message) {
        if (session == null) {
            LOGGER.error("session == null");
            return;
        }
        try {
            session.getRemote().sendString(message);
        } catch (IOException e) {
            LOGGER.error("IOException");
        }
    }
}
