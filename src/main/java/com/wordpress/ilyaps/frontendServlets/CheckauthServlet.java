package com.wordpress.ilyaps.frontendServlets;

import com.wordpress.ilyaps.services.accountService.AccountService;
import com.wordpress.ilyaps.services.accountService.dataset.UserProfile;
import org.jetbrains.annotations.NotNull;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import static com.wordpress.ilyaps.frontendServlets.ServletsHelper.printInResponse;

/**
 * Created by ilya on 26.09.15.
 */
public class CheckauthServlet extends HttpServlet {
    private static final int DEFAULT_AMOUNT = 10;
    private static final int DEFAULT_START = 0;

    @NotNull
    private AccountService accService;

    public CheckauthServlet(@NotNull AccountService accService) {
        this.accService = accService;
    }

    @Override
    public void doGet(@NotNull HttpServletRequest request,
                      @NotNull HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        Map<String, Object> pageVariables = new HashMap<>();

        String sessionId = request.getSession().getId();
        UserProfile profile = accService.getAuthorizedUser(sessionId);

        if (profile == null) {
            pageVariables.put("status", HttpServletResponse.SC_UNAUTHORIZED);
            pageVariables.put("info", "you not aUTHORIZED");
        } else {
            pageVariables.put("status", HttpServletResponse.SC_OK);
            pageVariables.put("info", profile.getName());
        }

        printInResponse(pageVariables, response);
    }

}
