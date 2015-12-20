package com.wordpress.ilyaps.frontendServlets;

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
 * Created by v.chibrikov on 13.09.2014.
 */
public class RegisterServlet extends HttpServlet {
    @NotNull
    static final Logger LOGGER = LogManager.getLogger(RegisterServlet.class);
    @NotNull
    private final ServletsService srvService;

    public RegisterServlet(@NotNull ServletsService srvService) {
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

        String sessionId = request.getSession().getId();
        String nameInSession = ServletsHelper.getNameInSession(request);

        if (ServletsHelper.nameEqualsIncognitto(nameInSession)) {
            srvService.removeUserProfile(sessionId);
            ServletsHelper.signupInResponse(response);
            return;
        }

        pageVariables.put("status", ServletsHelper.STATUSTEAPOT);
        pageVariables.put("info", "You already autherizate");
        ServletsHelper.printInResponse(pageVariables, response);
    }

    @Override
    public void doPost(@NotNull HttpServletRequest request,
                       @NotNull HttpServletResponse response)
            throws ServletException, IOException
    {
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        Map<String, Object> pageVariables = new HashMap<>();

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        String sessionId = request.getSession().getId();

        String info = checkParameters(name, email, password);
        if (info != null) {
            pageVariables.put("status", HttpServletResponse.SC_BAD_REQUEST);
            pageVariables.put("info", info);

            printInResponse(pageVariables, response);
            return;
        }

        UserState state = srvService.getUserState(email);

        if (state == UserState.PENDING_REGISTRATION) {
            LOGGER.info("user pands registration");
            pageVariables.put("status", HttpServletResponse.SC_NOT_MODIFIED);
            pageVariables.put("info", "your registration not ready.");

            printInResponse(pageVariables, response);
            return;
        }

        if (state == UserState.SUCCESSFUL_REGISTERED) {
            LOGGER.info("successful registration " + email);
            pageVariables.put("status", HttpServletResponse.SC_OK);
            pageVariables.put("info", email);
            srvService.removeUserState(email);
            srvService.removeUserProfile(sessionId);

            printInResponse(pageVariables, response);
            return;
        }

        if (state == UserState.UNSUCCESSFUL_REGISTERED) {
            LOGGER.info("user with this name or email already exists");
            pageVariables.put("status", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            pageVariables.put("info", "user with this name or email already exists");
            srvService.removeUserState(email);
            srvService.removeUserProfile(sessionId);

            printInResponse(pageVariables, response);
            return;
        }

        srvService.registerUser(name, email, password);
        pageVariables.put("status", HttpServletResponse.SC_NOT_MODIFIED);
        pageVariables.put("info", "your registration not ready.");
        printInResponse(pageVariables, response);
    }



    @Nullable
    String checkParameters(String name, String email, String password)
    {
        if (name == null || password == null || email == null) {
            LOGGER.info("name or email or password is null");
            return "name or email or password is null";
        }

        if (name.length() < 4 || email.length() < 4 || password.length() < 4) {
            LOGGER.info("name or email or password is short");
            return "name or email or password is short";
        }

        if (Pattern.matches(ServletsHelper.CYRILLIC_PATTERN, name) ||
                Pattern.matches(ServletsHelper.CYRILLIC_PATTERN, email))
        {
            LOGGER.info("not supported Cyrillic");
            return "not supported Cyrillic";
        }

        return null;
    }


}
