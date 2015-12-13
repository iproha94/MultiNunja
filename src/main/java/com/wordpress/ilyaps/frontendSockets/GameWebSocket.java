package com.wordpress.ilyaps.frontendSockets;

import com.wordpress.ilyaps.frontendService.FrontendService;
import com.wordpress.ilyaps.serverHelpers.GameContext;
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
    private final WebSocketService webSocketService;
    @NotNull
    private final FrontendService feService;
    private Session session;

    public GameWebSocket(@NotNull FrontendService feService, @NotNull String myName) {
        this.myName = myName;
        this.feService = feService;

        GameContext gameContext = GameContext.getInstance();
        this.webSocketService = (WebSocketService) gameContext.get(WebSocketService.class);
    }

    @NotNull
    public String getMyName() {
        return myName;
    }

    @OnWebSocketConnect
    public void onOpen(@NotNull Session newSession) {
        this.session = newSession;
        webSocketService.add(this);


        feService.openSocket(myName);
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        feService.closeSocket(myName);
        webSocketService.remove(myName);
    }

    @OnWebSocketMessage
    public void onMessage(String data) {
        if (data == null) {
            return;
        }
        feService.receiveData(myName, data);
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
