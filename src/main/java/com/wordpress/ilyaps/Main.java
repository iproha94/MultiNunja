package com.wordpress.ilyaps;

import com.wordpress.ilyaps.accountService.AccountService;
import com.wordpress.ilyaps.accountService.AccountServiceDAO;
import com.wordpress.ilyaps.accountService.AccountServiceDAOImpl1;
import com.wordpress.ilyaps.accountService.AccountServiceImpl1;
import com.wordpress.ilyaps.frontendService.FrontendService;
import com.wordpress.ilyaps.frontendService.FrontendServiceImpl1;
import com.wordpress.ilyaps.frontendServlets.*;
import com.wordpress.ilyaps.frontendSockets.WebSocketService;
import com.wordpress.ilyaps.frontendSockets.WebSocketServiceImpl;
import com.wordpress.ilyaps.gamemechService.GamemechMultiNunja;
import com.wordpress.ilyaps.gamemechService.GamemechService;
import com.wordpress.ilyaps.gamemechService.GamemechServiceImpl1;
import com.wordpress.ilyaps.gamemechService.SpecificGame;
import com.wordpress.ilyaps.messageSystem.MessageSystem;
import com.wordpress.ilyaps.resourceSystem.ResourcesContext;
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

        final GameContext gameСontext = GameContext.getInstance();

        final Configuration conf = new Configuration(PROPERTIES_FILE);
        gameСontext.add(Configuration.class, conf);

        final WebSocketService webSocketService = new WebSocketServiceImpl();
        gameСontext.add(WebSocketService.class, webSocketService);

        final ResourcesContext resourcesContext = new ResourcesContext(conf.getValueOfProperty("resourcesDirectory"));
        gameСontext.add(ResourcesContext.class, resourcesContext);

        final MessageSystem messageSystem = new MessageSystem();
        gameСontext.add(MessageSystem.class, messageSystem);

        final FrontendService frontendService = new FrontendServiceImpl1();
        final Thread frontendServiceThread = new Thread(frontendService);
        frontendServiceThread.setDaemon(true);
        frontendServiceThread.setName("frontendService");
        gameСontext.add(FrontendService.class, frontendService);


        final AccountServiceDAO accountServiceDAO = new AccountServiceDAOImpl1();
        final AccountService accountService = new AccountServiceImpl1(accountServiceDAO);
        final Thread accountServiceThread = new Thread(accountService);
        accountServiceThread.setDaemon(true);
        accountServiceThread.setName("Account Service");
        gameСontext.add(AccountService.class, accountService);

        final GamemechService gamemechService = new GamemechServiceImpl1();
        final Thread gamemechServiceThread = new Thread(gamemechService);
        gamemechServiceThread.setDaemon(true);
        gamemechServiceThread.setName("Gamemech Service");
        gameСontext.add(GamemechService.class, gamemechService);

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
