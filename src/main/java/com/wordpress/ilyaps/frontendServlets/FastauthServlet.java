package com.wordpress.ilyaps.frontendServlets;

import com.wordpress.ilyaps.services.accountService.AccountService;
import com.wordpress.ilyaps.services.accountService.dataset.UserProfile;
import com.wordpress.ilyaps.services.servletsService.ServletsService;
import com.wordpress.ilyaps.services.servletsService.UserState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.wordpress.ilyaps.frontendServlets.ServletsHelper.printInResponse;

/**
 * @author v.chibrikov
 */
public class FastauthServlet extends HttpServlet {
    @NotNull
    static final Logger LOGGER = LogManager.getLogger(FastauthServlet.class);
    @NotNull
    private final AccountService accService;
    @NotNull
    private final ServletsService srvService;

    public FastauthServlet(@NotNull AccountService accService,
                           @NotNull ServletsService srvService) {
        this.accService = accService;
        this.srvService = srvService;
    }


    @Override
    public void doGet(@NotNull HttpServletRequest request,
                      @NotNull HttpServletResponse response)
            throws ServletException, IOException
    {
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        Map<String, Object> pageVariables = new HashMap<>();

        String name = ServletsHelper.getNameInSession(request);
        String sessionId = request.getSession().getId();

        if (ServletsHelper.nameEqualsIncognitto(name)) {
            ServletsHelper.fastauthInResponse(response);
        } else {
            pageVariables.put("status", ServletsHelper.STATUSTEAPOT);
            pageVariables.put("info", "You already autherizate");
            ServletsHelper.printInResponse(pageVariables, response);
        }
    }

    @Override
    public void doPost(@NotNull HttpServletRequest request,
                       @NotNull HttpServletResponse response)
            throws ServletException, IOException
    {
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        Map<String, Object> pageVariables = new HashMap<>();

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        String info = checkParameters(username, password);
        if (info != null) {
            pageVariables.put("status", HttpServletResponse.SC_BAD_REQUEST);
            pageVariables.put("info", info);

            printInResponse(pageVariables, response);
            return;
        }

        String sessionId = request.getSession().getId();

        UserProfile profile = accService.fastauthorization(sessionId, username, password);

        if (profile == null) {
            pageVariables.put("status", HttpServletResponse.SC_BAD_REQUEST);
            pageVariables.put("info", "user with this name already exists");
        } else {
            srvService.addSession(sessionId, profile);
            srvService.addState(profile.getEmail(), UserState.SUCCESSFUL_AUTHORIZED);

            pageVariables.put("status", HttpServletResponse.SC_OK);
            pageVariables.put("info", profile.getName());

            request.getSession().setAttribute("name", profile.getName());
            LOGGER.info("successful fastauthorization " + profile.getName());
        }

        printInResponse(pageVariables, response);
    }

    @Nullable
    String checkParameters(String username, String password) {
        if (password == null || username == null) {
            LOGGER.info("username or password is null");
            return "username or password is null";
        }

        if (username.length() < 4 || password.length() < 4) {
            LOGGER.info("username or password is short");
            return "username or password is short";
        }

        if (Pattern.matches(ServletsHelper.CYRILLIC_PATTERN, username)) {
            LOGGER.info("not supported Cyrillic");
            return "not supported Cyrillic";
        }

        return null;
    }

}
