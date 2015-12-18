package com.wordpress.ilyaps.frontendServlets;

import com.wordpress.ilyaps.services.servletsService.ScoreState;
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
public class ScoresServlet extends HttpServlet {
    private static final int DEFAULT_AMOUNT = 100;
    private static final int DEFAULT_START = 1;

    @NotNull
    static final Logger LOGGER = LogManager.getLogger(RegisterServlet.class);
    @NotNull
    private ServletsService srvService;

    public ScoresServlet(@NotNull ServletsService srvService) {
        this.srvService = srvService;
    }

    @Override
    public void doGet(@NotNull HttpServletRequest request,
                      @NotNull HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        String sessionId = request.getSession().getId();

        Map<String, Object> pageVariables = new HashMap<>();

        ScoreState state = srvService.checkScoreState(sessionId);

        if (state == ScoreState.SUCCESSFUL) {
            String scores = srvService.getScore(sessionId);
            try (PrintWriter pw = response.getWriter()) {
                pw.println(scores);
                return;
            }
        } else if (checkState(pageVariables, state)) {
            int amount;
            int start;

            if (request.getParameter("amount") != null) {
                amount = new Integer(request.getParameter("amount") );
            } else {
                amount = DEFAULT_AMOUNT;
            }

            if (request.getParameter("start") != null) {
                start = new Integer(request.getParameter("start") );
            } else {
                start = DEFAULT_START;
            }

            srvService.gettingScoreUser(sessionId, start, amount);
            pageVariables.put("status", HttpServletResponse.SC_NOT_MODIFIED);
            pageVariables.put("info", "wait completed getting scores");
        }

        try (PrintWriter pw = response.getWriter()) {
            pw.println(PageGenerator.getPage("authresponse.txt", pageVariables));
        }
    }

    boolean checkState(Map<String, Object> pageVariables, ScoreState state) {
        if (state == ScoreState.UNSUCCESSFUL) {
            LOGGER.warn("unsuccessful getting scores");
            pageVariables.put("status", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            pageVariables.put("info", "unsuccessful getting scores");
            return false;
        } else if (state == ScoreState.PENDING) {
            LOGGER.warn("user pands getting scores");
            pageVariables.put("status", HttpServletResponse.SC_NOT_MODIFIED);
            pageVariables.put("info", "user pands getting scores");
            return false;
        }

        return true;
    }
}
