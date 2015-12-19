package com.wordpress.ilyaps.frontendServlets;

import com.wordpress.ilyaps.services.accountService.AccountService;
import com.wordpress.ilyaps.services.servletsService.ServletsService;
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

public class AdminpageServlet extends HttpServlet {
    @NotNull
    static final String PASSWORD = "1489";
    @NotNull
    static final Logger LOGGER = LogManager.getLogger(RegisterServlet.class);

    @NotNull
    private AccountService accService;

    public AdminpageServlet(@NotNull AccountService accService) {
        this.accService = accService;
    }

    @Override
    public void doGet(@NotNull HttpServletRequest request,
                      @NotNull HttpServletResponse response) throws ServletException, IOException {
        String nameInSession = (String) request.getSession().getAttribute("name");

        LOGGER.info(nameInSession + " went to the admin panel");

        response.setContentType("text/html;charset=utf-8");
        response.setCharacterEncoding("utf-8");

        Map<String, Object> pageVariables = new HashMap<>();

        pageVariables.put("status", "run");
        pageVariables.put("countUser", accService.getAccountServiceDAO().countRegisteredUser());
        pageVariables.put("countSession", accService.getAccountServiceDAO().countAuthorizedUser());

        try (PrintWriter pw = response.getWriter()) {
            pw.println(PageGenerator.getPage("admin/admin.html", pageVariables));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=utf-8");

        String nameInSession = (String) req.getSession().getAttribute("name");
        String password = req.getParameter("password");

        LOGGER.info(nameInSession + " trying to shut down the server");
        Map<String, Object> pageVariables = new HashMap<>();

        if (!PASSWORD.equals(password)) {
            LOGGER.info("not right password for shutdown server");

            pageVariables.put("status", HttpServletResponse.SC_BAD_REQUEST);
            pageVariables.put("info", "not right password");

            try (PrintWriter pw = resp.getWriter()) {
                pw.println(PageGenerator.getPage("authresponse.txt", pageVariables));
            }
        } else {
            LOGGER.info("Shutdown server successful");
            System.exit(0);
        }
    }
}

