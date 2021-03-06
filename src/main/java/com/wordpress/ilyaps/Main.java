package com.wordpress.ilyaps;

import com.wordpress.ilyaps.frontendServlets.*;
import com.wordpress.ilyaps.messageSystem.MessageSystem;
import com.wordpress.ilyaps.multiNunjaGamemech.MultiNunjaGamemech;
import com.wordpress.ilyaps.resourceSystem.ResourcesContext;
import com.wordpress.ilyaps.serverHelpers.Configuration;
import com.wordpress.ilyaps.serverHelpers.GameContext;
import com.wordpress.ilyaps.services.accountService.*;
import com.wordpress.ilyaps.services.gamemechService.GamemechService;
import com.wordpress.ilyaps.services.servletsService.ServletsService;
import com.wordpress.ilyaps.services.servletsService.ServletsServiceImpl;
import com.wordpress.ilyaps.services.socketsService.SocketsService;
import com.wordpress.ilyaps.services.socketsService.SocketsServiceImpl;
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
    private static final String PROPERTIES_FILE_DB = "cfg/db.properties";
    @NotNull
    private static final String PROPERTIES_FILE = "cfg/server.properties";

    public static void main(@NotNull String[] args) {
        LOGGER.info("start of activation of server");

        final GameContext GAMECONTEXT = GameContext.getInstance();

        final Configuration conf = new Configuration(PROPERTIES_FILE);
        GAMECONTEXT.add(Configuration.class, conf);

        final ResourcesContext resourcesContext = new ResourcesContext(conf.getValueOfProperty("resourcesDirectory"));
        GAMECONTEXT.add(ResourcesContext.class, resourcesContext);

        final MessageSystem messageSystem = new MessageSystem();
        GAMECONTEXT.add(MessageSystem.class, messageSystem);

        final SocketsService socketsService = new SocketsServiceImpl();
        final Thread socketsServiceThread = new Thread(socketsService);
        socketsServiceThread.setDaemon(true);
        socketsServiceThread.setName("socketsService");
        GAMECONTEXT.add(SocketsService.class, socketsService);

        final ServletsService servletsService = new ServletsServiceImpl();
        final Thread servletsServiceThread = new Thread(servletsService);
        servletsServiceThread.setDaemon(true);
        servletsServiceThread.setName("servletsService");
        GAMECONTEXT.add(ServletsService.class, servletsService);

        final AccountService accountService = new AccountServiceDB(PROPERTIES_FILE_DB);
        final Thread accountServiceThread = new Thread(accountService);
        accountServiceThread.setDaemon(true);
        accountServiceThread.setName("Account Service");
        GAMECONTEXT.add(AccountService.class, accountService);

        final GamemechService gamemechService = new MultiNunjaGamemech();
        final Thread gamemechServiceThread = new Thread(gamemechService);
        gamemechServiceThread.setDaemon(true);
        gamemechServiceThread.setName("Gamemech Service");
        GAMECONTEXT.add(GamemechService.class, gamemechService);


        Servlet mainPage = new MainpageServlet();
        Servlet signUp = new RegisterServlet(servletsService);
        Servlet signIn = new AuthorizationServlet(servletsService);
        Servlet logout = new LeavingServlet(servletsService);
        Servlet admin = new AdminpageServlet();
        Servlet scores = new ScoresServlet(accountService);
        Servlet checkauth = new CheckauthServlet(accountService);
        Servlet fastauth = new FastauthServlet(accountService, servletsService);
        WebSocketServlet game = new GameServlet();

        final ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);

        context.addServlet(new ServletHolder(mainPage), conf.getValueOfProperty("mainPageUrl"));
        context.addServlet(new ServletHolder(signUp), conf.getValueOfProperty("signupPageUrl"));
        context.addServlet(new ServletHolder(signIn), conf.getValueOfProperty("signinPageUrl"));
        context.addServlet(new ServletHolder(logout), conf.getValueOfProperty("logoutPageUrl"));
        context.addServlet(new ServletHolder(game), conf.getValueOfProperty("gameSocketUrl"));
        context.addServlet(new ServletHolder(admin), conf.getValueOfProperty("adminPageUrl"));
        context.addServlet(new ServletHolder(scores), conf.getValueOfProperty("scoresPageUrl"));
        context.addServlet(new ServletHolder(checkauth), conf.getValueOfProperty("checkauthPageUrl"));
        context.addServlet(new ServletHolder(fastauth), conf.getValueOfProperty("fastauthPageUrl"));


        final ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setResourceBase("static");

        final Server server = new Server(new Integer(conf.getValueOfProperty("port")));
        final HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resourceHandler, context});
        server.setHandler(handlers);

        socketsServiceThread.start();
        servletsServiceThread.start();
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
