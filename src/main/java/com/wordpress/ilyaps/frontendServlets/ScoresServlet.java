package com.wordpress.ilyaps.frontendServlets;

import com.wordpress.ilyaps.services.accountService.AccountService;
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
    private static final int DEFAULT_START = 0;

    @NotNull
    private final AccountService accService;

    public ScoresServlet(@NotNull AccountService accService) {
        this.accService = accService;
    }

    @Override
    public void doGet(@NotNull HttpServletRequest request,
                      @NotNull HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        int amount;
        try {
            amount = Integer.parseInt(request.getParameter("amount"));
        } catch (NumberFormatException e) {
            amount = DEFAULT_AMOUNT;
        }

        int start;
        try {
            start = Integer.parseInt(request.getParameter("start"));
        } catch (NumberFormatException e) {
            start = DEFAULT_START;
        }

        String name = request.getParameter("name");

        String msg;
        if (name != null) {
            msg = accService.getScore(name);
        } else {
            msg = accService.getScore(start, amount);
        }

        try (PrintWriter pw = response.getWriter()) {
            pw.println(msg);
        }
    }

}
