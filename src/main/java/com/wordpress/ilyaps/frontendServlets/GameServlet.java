package com.wordpress.ilyaps.frontendServlets;

import com.wordpress.ilyaps.frontendSockets.GameWebSocketCreator;
import com.wordpress.ilyaps.serverHelpers.Configuration;
import com.wordpress.ilyaps.serverHelpers.GameContext;
import com.wordpress.ilyaps.utils.PageGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.jetbrains.annotations.NotNull;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ilya on 27.10.15.
 */
public class GameServlet extends WebSocketServlet {
    @NotNull
    static final Logger LOGGER = LogManager.getLogger(GameServlet.class);

    private static final int IDLE_TIME = 10 * 60 * 1000;

    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.getPolicy().setIdleTimeout(IDLE_TIME);
        factory.setCreator(new GameWebSocketCreator());
    }

    @Override
    public void doGet(@NotNull HttpServletRequest request,
                      @NotNull HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        String name = ServletsHelper.getNameInSession(request);
        if (ServletsHelper.nameEqualsIncognitto(name)) {
            LOGGER.info("name == Incognitto");
            return;
        }

        Configuration conf = (Configuration) GameContext.getInstance().get(Configuration.class);

        Map<String, Object> pageVariables = new HashMap<>();
        pageVariables.put("name", name);
        pageVariables.put("host_game", conf.getValueOfProperty("gameSocketHost"));
        pageVariables.put("port_game", conf.getValueOfProperty("gameSocketPort"));
        pageVariables.put("url_game", conf.getValueOfProperty("gameSocketUrl"));

        try (PrintWriter pw = response.getWriter()) {
            pw.println(PageGenerator.getPage("game.html", pageVariables));
        }
    }
}