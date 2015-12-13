package com.wordpress.ilyaps.frontendSockets;

import com.wordpress.ilyaps.ThreadSettings;
import com.wordpress.ilyaps.accountService.UserProfile;
import com.wordpress.ilyaps.frontendService.FrontendService;
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
    private FrontendService feService;

    public GameWebSocketCreator(@NotNull FrontendService feService) {
        this.feService = feService;
    }

    @Override
    public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp) {
        String sessionId = req.getHttpServletRequest().getSession().getId();

        feService.gettingUserProfile(sessionId);

        while (!feService.endedGettingUserProfile(sessionId)) {
            LOGGER.info("ждем окончание getting");
            try {
                synchronized (this) {
                    this.wait(ThreadSettings.SLEEP_TIME);
                }
            } catch (InterruptedException e) {
                LOGGER.error("wait потока сервлета");
                LOGGER.error(e);
            }
        }
        LOGGER.info("getting закончился");

        UserProfile profile = feService.successfulGettingUserProfile(sessionId);

        return new GameWebSocket(feService, profile.getName());
    }
}
