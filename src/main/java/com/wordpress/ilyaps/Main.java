package com.wordpress.ilyaps;

import com.wordpress.ilyaps.accountService.AccountServiceDAO;
import com.wordpress.ilyaps.accountService.AccountServiceDAOImpl1;
import com.wordpress.ilyaps.accountService.AccountServiceImpl1;
import com.wordpress.ilyaps.frontendService.FrontendService;
import com.wordpress.ilyaps.frontendService.FrontendServiceImpl1;
import com.wordpress.ilyaps.frontendServlets.MainpageServlet;
import com.wordpress.ilyaps.frontendServlets.RegisterServlet;
import com.wordpress.ilyaps.messageSystem.MessageSystem;
import com.wordpress.ilyaps.serverHelpers.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
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

        Configuration conf = new Configuration(PROPERTIES_FILE);

        final Server server = new Server(new Integer(conf.getValueOfProperty("port")));

        final MessageSystem messageSystem = new MessageSystem();

        final FrontendService frontendService = new FrontendServiceImpl1(messageSystem);
        final Thread frontendServiceThread = new Thread(frontendService);
        frontendServiceThread.setDaemon(true);
        frontendServiceThread.setName("frontendService");

        final AccountServiceDAO accountServiceDAO = new AccountServiceDAOImpl1();
        final Thread accountServiceThread = new Thread(new AccountServiceImpl1(messageSystem, accountServiceDAO));
        accountServiceThread.setDaemon(true);
        accountServiceThread.setName("Account Service");

        //

        Servlet mainPage = new MainpageServlet();
        Servlet signUp = new RegisterServlet(frontendService);
        //


        final ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);

        context.addServlet(new ServletHolder(mainPage), conf.getValueOfProperty("mainPageUrl"));
        context.addServlet(new ServletHolder(signUp), conf.getValueOfProperty("signupPageUrl"));
        //


        final ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setResourceBase("static");

        final HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resourceHandler, context});
        server.setHandler(handlers);

        frontendServiceThread.start();
        accountServiceThread.start();
        //


        try {
            server.start();
        } catch (Exception e) {
            LOGGER.error("Server isn't started");
            LOGGER.error(e);
        }
    }
}
