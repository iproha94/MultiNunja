package com.wordpress.ilyaps.frontendSockets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ilya on 27.10.15.
 */
public class WebSocketServiceImpl implements WebSocketService {
    @NotNull
    static final Logger LOGGER = LogManager.getLogger(WebSocketServiceImpl.class);

    @NotNull
    private final Map<String, GameWebSocket> userSockets = new HashMap<>();

    @Override
    public void add(@NotNull GameWebSocket userSocket) {
        userSockets.put(userSocket.getMyName(), userSocket);
    }

    @Override
    public boolean remove(@NotNull String name) {
        return userSockets.remove(name) != null;
    }

    @Override
    public void notify(@NotNull String userName, @NotNull String message) {
        GameWebSocket gameWebSocket = userSockets.get(userName);
        if (gameWebSocket == null) {
            LOGGER.error("gameWebSocket == null");
            return;
        }

        gameWebSocket.send(message);
    }

}