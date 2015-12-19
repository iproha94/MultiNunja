package com.wordpress.ilyaps.frontendServlets;

import com.wordpress.ilyaps.services.accountService.UserProfile;
import com.wordpress.ilyaps.services.servletsService.ServletsService;
import com.wordpress.ilyaps.services.servletsService.UserState;
import com.wordpress.ilyaps.utils.PageGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author v.chibrikov
 */
public class AuthorizationServlet extends HttpServlet {
    private static final int STATUSTEAPOT = 418;
    private static final String INCOGNITTO = "Incognitto";


    @NotNull
    static final Logger LOGGER = LogManager.getLogger(RegisterServlet.class);
    @NotNull
    private ServletsService feService;

    public AuthorizationServlet(@NotNull ServletsService feService) {
        this.feService = feService;
    }

    @Override
    public void doGet(@NotNull HttpServletRequest request,
                      @NotNull HttpServletResponse response)
            throws ServletException, IOException
    {
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        Map<String, Object> pageVariables = new HashMap<>();

        String nameInSession = (String) request.getSession().getAttribute("name");

        try (PrintWriter pw = response.getWriter()) {
            if (checkNameInSession(pageVariables, nameInSession)) {
                pw.println(PageGenerator.getPage("auth/signin.html", pageVariables));
            } else {
                pw.println(PageGenerator.getPage("authresponse.txt", pageVariables));
            }
        }
    }

    @Override
    public void doPost(@NotNull HttpServletRequest request,
                       @NotNull HttpServletResponse response)
            throws ServletException, IOException
    {
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String nameInSession = (String) request.getSession().getAttribute("name");
        String sessionId = request.getSession().getId();

        Map<String, Object> pageVariables = new HashMap<>();
        UserState state = feService.checkUserState(email);

        if (state == UserState.SUCCESSFUL_AUTHORIZED) {
            UserProfile profile = feService.getUser(sessionId);
            request.getSession().setAttribute("name", profile.getName());

            LOGGER.info("successful authorization");
            pageVariables.put("status", HttpServletResponse.SC_OK);
            pageVariables.put("info", profile.getName());

        } else if (checkNameInSession(pageVariables, nameInSession) &&
                checkState(pageVariables, state) &&
                checkParameters(pageVariables, email, password) )
        {
            LOGGER.info("start autharization");
            feService.authorizationUser(email, password, sessionId);

            pageVariables.put("status", HttpServletResponse.SC_NOT_MODIFIED);
            pageVariables.put("info", "wait completed autharization");
        }

        try (PrintWriter pw = response.getWriter()) {
            pw.println(PageGenerator.getPage("authresponse.txt", pageVariables));
        }
    }

    boolean checkNameInSession(Map<String, Object> pageVariables, String name) {
        if (name != null && !INCOGNITTO.equals(name)) {
            LOGGER.info("the user has already been authenticated");
            pageVariables.put("status", STATUSTEAPOT);
            pageVariables.put("info", "you has already been authenticated");
            return false;
        }

        return true;
    }

    boolean checkState(Map<String, Object> pageVariables, UserState state) {
        if (state == UserState.UNSUCCESSFUL_AUTHORIZED) {
            LOGGER.warn("user with this email and password not found");
            pageVariables.put("status", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            pageVariables.put("info", "user with this email and password not found");
            return false;
        } else if (state == UserState.PENDING_AUTHORIZATION) {
            LOGGER.warn("user pands authorization");
            pageVariables.put("status", HttpServletResponse.SC_NOT_MODIFIED);
            pageVariables.put("info", "your authorization not ready.");
            return false;
        }

        return true;
    }

    boolean checkParameters(Map<String, Object> pageVariables, String email, String password) {
        if (password == null || email == null) {
            LOGGER.info("email or password is null");
            pageVariables.put("status", HttpServletResponse.SC_BAD_REQUEST);
            pageVariables.put("info", "email or password is null");
            return false;
        } else if (email.length() < 4 || password.length() < 4) {
            LOGGER.info("email or password is short");
            pageVariables.put("status", HttpServletResponse.SC_BAD_REQUEST);
            pageVariables.put("info", "email or password is short");
            return false;
        } else if (Pattern.matches(".*[А-Яа-я]+.*", email)) {
            LOGGER.info("not supported Cyrillic");
            pageVariables.put("status", HttpServletResponse.SC_BAD_REQUEST);
            pageVariables.put("info", "not supported Cyrillic");
            return false;
        }

        return true;
    }

}
