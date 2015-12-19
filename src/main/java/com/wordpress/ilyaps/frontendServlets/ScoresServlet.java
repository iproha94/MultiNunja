package com.wordpress.ilyaps.frontendServlets;

import com.wordpress.ilyaps.services.accountService.AccountService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by ilya on 26.09.15.
 */
public class ScoresServlet extends HttpServlet {
    private static final int DEFAULT_AMOUNT = 10;
    private static final int DEFAULT_START = 1;

    @NotNull
    static final Logger LOGGER = LogManager.getLogger(RegisterServlet.class);
    @NotNull
    private AccountService accService;

    public ScoresServlet(@NotNull AccountService accService) {
        this.accService = accService;
    }

    @Override
    public void doGet(@NotNull HttpServletRequest request,
                      @NotNull HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        String listScore = accService.getAccountServiceDAO().getScore(DEFAULT_START, DEFAULT_AMOUNT);
        try (PrintWriter pw = response.getWriter()) {
            pw.println(listScore);
        }
    }

}
