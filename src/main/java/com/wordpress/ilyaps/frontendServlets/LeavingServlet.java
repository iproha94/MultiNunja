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

/**
 * Created by ilya on 26.09.15.
 */
public class LeavingServlet extends HttpServlet {
    private static final int STATUSTEAPOT = 418;
    private static final String INCOGNITTO = "Incognitto";

    @NotNull
    static final Logger LOGGER = LogManager.getLogger(RegisterServlet.class);
    @NotNull
    private ServletsService feService;

    public LeavingServlet(@NotNull ServletsService feService) {
        this.feService = feService;
    }

    @Override
    public void doGet(@NotNull HttpServletRequest request,
                      @NotNull HttpServletResponse response)
            throws ServletException, IOException
    {
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        String nameInSession = (String) request.getSession().getAttribute("name");
        String sessionId = request.getSession().getId();

        Map<String, Object> pageVariables = new HashMap<>();

        UserProfile profile = feService.getUser(sessionId);

        if (!checkNameInSession(pageVariables, nameInSession)) {
            LOGGER.info("up button exit");
        } else {
            if (profile == null) {
                LOGGER.warn("profile == null");
                return;
            }

            UserState state = feService.checkUserState(profile.getEmail());

            if (state == UserState.LEFT) {
                LOGGER.info("successful leaving");
                pageVariables.put("status", HttpServletResponse.SC_OK);
                pageVariables.put("info", "come back soon");

                request.getSession().setAttribute("name", INCOGNITTO);
            } else if (checkNameInSession(pageVariables, nameInSession) &&
                    checkState(pageVariables, state))
            {
                feService.leaveUser(profile.getEmail(), sessionId);

                pageVariables.put("status", HttpServletResponse.SC_NOT_MODIFIED);
                pageVariables.put("info", "wait completed leaving");
            }
        }

        try (PrintWriter pw = response.getWriter()) {
            pw.println(PageGenerator.getPage("authresponse.txt", pageVariables));
        }

    }

    boolean checkNameInSession(Map<String, Object> pageVariables, String name) {
        if (name == null || INCOGNITTO.equals(name)) {
            LOGGER.info("the user is already out");
            pageVariables.put("status", STATUSTEAPOT);
            pageVariables.put("info", "you is already out");
            return false;
        }

        return true;
    }

    boolean checkState(Map<String, Object> pageVariables, UserState state) {
        if (state == UserState.UNSUCCESSFUL_LEFT) {
            LOGGER.warn("unsuccessful left");
            pageVariables.put("status", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            pageVariables.put("info", "unsuccessful left");
            return false;
        } else if (state == UserState.PENDING_LEAVING) {
            LOGGER.warn("user pands leaving");
            pageVariables.put("status", HttpServletResponse.SC_NOT_MODIFIED);
            pageVariables.put("info", "your leaving not ready.");
            return false;
        }

        return true;
    }
}
