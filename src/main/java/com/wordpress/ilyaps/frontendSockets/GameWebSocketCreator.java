package com.wordpress.ilyaps.frontendSockets;

import com.wordpress.ilyaps.services.accountService.dataset.UserProfile;
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
    @NotNull
    static final Logger LOGGER = LogManager.getLogger(GameWebSocketCreator.class);
    @NotNull
    private final SocketsService sckService;
    @NotNull
    private final ServletsService srvService;

    public GameWebSocketCreator() {
        GameContext gameContext = GameContext.getInstance();

        this.sckService = (SocketsService) gameContext.get(SocketsService.class);
        this.srvService = (ServletsService) gameContext.get(ServletsService.class);
    }

    @Override
    public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp) {
        String sessionId = req.getHttpServletRequest().getSession().getId();

        UserProfile profile = srvService.getUserProfile(sessionId);

        if (profile == null) {
            LOGGER.error("profile == null");
            throw new NullPointerException();
        }
        return new GameWebSocket(sckService, profile.getName());
    }
}
