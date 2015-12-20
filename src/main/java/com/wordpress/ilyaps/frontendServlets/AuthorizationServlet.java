package com.wordpress.ilyaps.frontendServlets;

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
public class AuthorizationServlet extends HttpServlet {
    @NotNull
    static final Logger LOGGER = LogManager.getLogger(RegisterServlet.class);
    @NotNull
    private final ServletsService srvService;

    public AuthorizationServlet(@NotNull ServletsService srvService) {
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
            srvService.removeUserProfile(sessionId);
            ServletsHelper.signinInResponse(response);
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

        String email = request.getParameter("email");
        String password = request.getParameter("password");


        String info = checkParameters(email, password);
        if (info != null) {
            pageVariables.put("status", HttpServletResponse.SC_BAD_REQUEST);
            pageVariables.put("info", info);

            printInResponse(pageVariables, response);
            return;
        }

        UserState state = srvService.getUserState(email);

        if (state == UserState.PENDING_AUTHORIZATION) {
            LOGGER.info("user pands authorization");
            pageVariables.put("status", HttpServletResponse.SC_NOT_MODIFIED);
            pageVariables.put("info", "your authorization not ready.");

            printInResponse(pageVariables, response);
            return;
        }

        String sessionId = request.getSession().getId();
        UserProfile profile = srvService.getUserProfile(sessionId);

        if (state == UserState.SUCCESSFUL_AUTHORIZED) {
            if (profile == null) {

                srvService.authorizationUser(email, password, sessionId);
                pageVariables.put("status", HttpServletResponse.SC_NOT_MODIFIED);
                pageVariables.put("info", "your authorization not ready.");
                printInResponse(pageVariables, response);
                return;
            }

            request.getSession().setAttribute("name", profile.getName());
            pageVariables.put("status", HttpServletResponse.SC_OK);
            pageVariables.put("info", profile.getName());

            printInResponse(pageVariables, response);

            LOGGER.info("successful authorization " + email);
            return;
        }

        if (state == UserState.UNSUCCESSFUL_AUTHORIZED) {
            LOGGER.info("user with this email and password not found");
            pageVariables.put("status", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            pageVariables.put("info", "user with this email and password not found");

            srvService.removeUserState(email);
            srvService.removeUserProfile(sessionId);

            printInResponse(pageVariables, response);
            return;
        }

        srvService.authorizationUser(email, password, sessionId);
        pageVariables.put("status", HttpServletResponse.SC_NOT_MODIFIED);
        pageVariables.put("info", "your authorization not ready.");
        printInResponse(pageVariables, response);
    }

    @Nullable
    String checkParameters(String email, String password) {
        if (password == null || email == null) {
            LOGGER.info("email or password is null");
            return "email or password is null";
        }

        if (email.length() < 4 || password.length() < 4) {
            LOGGER.info("email or password is short");
            return "email or password is short";
        }

        if (Pattern.matches(ServletsHelper.CYRILLIC_PATTERN, email)) {
            LOGGER.info("not supported Cyrillic");
            return "not supported Cyrillic";
        }

        return null;
    }

}
