package com.wordpress.ilyaps.frontendServlets;

import com.wordpress.ilyaps.frontendService.FrontendService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by ilya on 26.09.15.
 */
public class ScoresServlet extends HttpServlet {
    private static final int DEFAULT_AMOUNT = 100;

    @NotNull
    static final Logger LOGGER = LogManager.getLogger(RegisterServlet.class);
    @NotNull
    private FrontendService feService;

    public ScoresServlet(@NotNull FrontendService feService) {
        this.feService = feService;
    }

    @Override
    public void doGet(@NotNull HttpServletRequest request,
                      @NotNull HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        Integer amount = DEFAULT_AMOUNT;
        String amountstr = request.getParameter("limit");
        if (amountstr != null) {
            amount = new Integer(amountstr);
        }
        if (amount < 1) {
            amount = DEFAULT_AMOUNT;
        }

        try (PrintWriter pw = response.getWriter()) {
//            pw.println(GameMessager.createMessageListScores(accountService, amount));
            pw.println("table empty");
        }
    }
}
