package com.wordpress.ilyaps.frontendSockets;

import com.wordpress.ilyaps.services.accountService.UserProfile;
import com.wordpress.ilyaps.serverHelpers.GameContext;
import com.wordpress.ilyaps.services.servletsService.ServletsService;
import com.wordpress.ilyaps.services.socketsService.SocketsService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.jetbrains.annotations.NotNull;

/**
 * Created by ilya on 27.10.15.
 */
public class GameWebSocketCreator implements WebSocketCreator {
    @SuppressWarnings("ConstantConditions")
    @NotNull
    static final Logger LOGGER = LogManager.getLogger(GameWebSocketCreator.class);
    @NotNull
    private SocketsService sckService;
    @NotNull
    private ServletsService srvService;

    public GameWebSocketCreator() {
        GameContext gameContext = GameContext.getInstance();

        this.sckService = (SocketsService) gameContext.get(SocketsService.class);
        this.srvService = (ServletsService) gameContext.get(ServletsService.class);
    }

    @Override
    public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp) {
        String sessionId = req.getHttpServletRequest().getSession().getId();

        UserProfile profile = srvService.getUser(sessionId);

        return new GameWebSocket(sckService, profile.getName());
    }
}
