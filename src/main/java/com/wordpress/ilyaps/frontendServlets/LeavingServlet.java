package com.wordpress.ilyaps.frontendServlets;

import com.wordpress.ilyaps.services.accountService.dataset.UserProfile;
import com.wordpress.ilyaps.services.servletsService.ServletsService;
import com.wordpress.ilyaps.services.servletsService.UserState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.wordpress.ilyaps.frontendServlets.ServletsHelper.printInResponse;

/**
 * Created by ilya on 26.09.15.
 */
public class LeavingServlet extends HttpServlet {
    @NotNull
    static final Logger LOGGER = LogManager.getLogger(RegisterServlet.class);
    @NotNull
    private ServletsService srvService;

    public LeavingServlet(@NotNull ServletsService srvService) {
        this.srvService = srvService;
    }

    @Override
    public void doGet(@NotNull HttpServletRequest request,
                      @NotNull HttpServletResponse response)
            throws ServletException, IOException
    {
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        String nameInSession =  ServletsHelper.getNameInSession(request);
        String sessionId = request.getSession().getId();

        Map<String, Object> pageVariables = new HashMap<>();

        if (ServletsHelper.nameEqualsIncognitto(nameInSession)) {
            srvService.removeUserProfile(sessionId);

            pageVariables.put("status", ServletsHelper.STATUSTEAPOT);
            pageVariables.put("info", "You already leaving");

            ServletsHelper.printInResponse(pageVariables, response);
            return;
        }

        UserProfile profile = srvService.getUserProfile(sessionId);

        if (profile == null) {
            srvService.removeUserProfile(sessionId);

            pageVariables.put("status", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            pageVariables.put("info", "error server");
            printInResponse(pageVariables, response);
            return;
        }

        UserState state = srvService.getUserState(profile.getEmail());

        if (state == UserState.PENDING_LEAVING) {
            LOGGER.info("user pands leaving");
            pageVariables.put("status", HttpServletResponse.SC_NOT_MODIFIED);
            pageVariables.put("info", "your leaving not ready.");
            printInResponse(pageVariables, response);
            return;
        }

        if (state == UserState.SUCCESSFUL_LEFT) {
            srvService.removeUserProfile(sessionId);
            srvService.removeUserState(profile.getEmail());

            pageVariables.put("status", HttpServletResponse.SC_OK);
            pageVariables.put("info", "come back soon");
            printInResponse(pageVariables, response);

            request.getSession().setAttribute("name", ServletsHelper.INCOGNITTO);

            LOGGER.info("successful leaving");
            return;
        }

        if (state == UserState.UNSUCCESSFUL_LEFT) {
            srvService.removeUserProfile(sessionId);
            srvService.removeUserState(profile.getEmail());

            pageVariables.put("status", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            pageVariables.put("info", "unsuccessful left");
            printInResponse(pageVariables, response);

            LOGGER.info("unsuccessful left");
        }

        srvService.leaveUser(profile.getEmail(), sessionId);
        pageVariables.put("status", HttpServletResponse.SC_NOT_MODIFIED);
        pageVariables.put("info", "your leaving not ready.");
        printInResponse(pageVariables, response);
    }
}
