package com.wordpress.ilyaps;

import com.wordpress.ilyaps.accountService.AccountServiceDAO;
import com.wordpress.ilyaps.accountService.AccountServiceDAOImpl1;
import com.wordpress.ilyaps.accountService.AccountServiceImpl1;
import com.wordpress.ilyaps.frontendService.FrontendService;
import com.wordpress.ilyaps.frontendService.FrontendServiceImpl1;
import com.wordpress.ilyaps.frontendServlets.*;
import com.wordpress.ilyaps.frontendSockets.WebSocketService;
import com.wordpress.ilyaps.frontendSockets.WebSocketServiceImpl;
import com.wordpress.ilyaps.gamemechService.GamemechServiceImpl1;
import com.wordpress.ilyaps.messageSystem.MessageSystem;
import com.wordpress.ilyaps.serverHelpers.Configuration;
import com.wordpress.ilyaps.serverHelpers.GameContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.jetbrains.annotations.NotNull;

import javax.servlet.Servlet;

/**
 * Created by ilya on 12.12.15.
 */
public class Main {
    @NotNull
    static final Logger LOGGER = LogManager.getLogger(Main.class);

    @NotNull
    private static final String PROPERTIES_FILE = "cfg/server.properties";

    public static void main(@NotNull String[] args) {
        LOGGER.info("старт сервера");

        GameContext gameСontext = GameContext.getInstance();

        Configuration conf = new Configuration(PROPERTIES_FILE);
        gameСontext.add(Configuration.class, conf);

        WebSocketService webSocketService = new WebSocketServiceImpl();
        gameСontext.add(WebSocketService.class, webSocketService);

        final MessageSystem messageSystem = new MessageSystem();

        final FrontendService frontendService = new FrontendServiceImpl1(messageSystem, webSocketService);
        final Thread frontendServiceThread = new Thread(frontendService);
        frontendServiceThread.setDaemon(true);
        frontendServiceThread.setName("frontendService");

        final AccountServiceDAO accountServiceDAO = new AccountServiceDAOImpl1();
        final Thread accountServiceThread = new Thread(new AccountServiceImpl1(messageSystem, accountServiceDAO));
        accountServiceThread.setDaemon(true);
        accountServiceThread.setName("Account Service");

        final Thread gamemechServiceThread = new Thread(new GamemechServiceImpl1(messageSystem));
        gamemechServiceThread.setDaemon(true);
        gamemechServiceThread.setName("Gamemech Service");


        final Server server = new Server(new Integer(conf.getValueOfProperty("port")));

        Servlet mainPage = new MainpageServlet();
        Servlet signUp = new RegisterServlet(frontendService);
        Servlet signIn = new AuthorizationServlet(frontendService);
        Servlet logout = new LeavingServlet(frontendService);
        WebSocketServlet game = new GameWebSocketServlet(frontendService);
//


        final ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);

        context.addServlet(new ServletHolder(mainPage), conf.getValueOfProperty("mainPageUrl"));
        context.addServlet(new ServletHolder(signUp), conf.getValueOfProperty("signupPageUrl"));
        context.addServlet(new ServletHolder(signIn), conf.getValueOfProperty("signinPageUrl"));
        context.addServlet(new ServletHolder(logout), conf.getValueOfProperty("logoutPageUrl"));
        context.addServlet(new ServletHolder(game), conf.getValueOfProperty("gameSocketUrl"));
//


        final ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setResourceBase("static");

        final HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resourceHandler, context});
        server.setHandler(handlers);

        frontendServiceThread.start();
        accountServiceThread.start();
        gamemechServiceThread.start();

        try {
            server.start();
        } catch (Exception e) {
            LOGGER.error("Server isn't started");
            LOGGER.error(e);
        }
    }
}
