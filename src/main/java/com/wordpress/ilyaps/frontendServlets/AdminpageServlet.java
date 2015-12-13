package com.wordpress.ilyaps.frontendServlets;

import com.wordpress.ilyaps.frontendService.FrontendService;
import com.wordpress.ilyaps.utils.PageGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
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
    static final Logger LOGGER = LogManager.getLogger(RegisterServlet.class);
    @NotNull
    private FrontendService feService;
    @NotNull
    private final Server server;

    public AdminpageServlet(@NotNull FrontendService feService, @NotNull Server server) {
        this.feService = feService;
        this.server = server;
    }

    @Override
    public void doGet(@NotNull HttpServletRequest request,
                      @NotNull HttpServletResponse response) throws ServletException, IOException {
        LOGGER.info("кто то лезет в админку");
        response.setContentType("text/html;charset=utf-8");
        response.setCharacterEncoding("utf-8");

        Map<String, Object> pageVariables = new HashMap<>();

        pageVariables.put("status", "run");
        //pageVariables.put("countUser", accountService.countUsers());
        //pageVariables.put("countSession", accountService.countSessions());
        pageVariables.put("countUser", 777);
        pageVariables.put("countSession", 777);

        try (PrintWriter pw = response.getWriter()) {
            pw.println(PageGenerator.getPage("admin/admin.html", pageVariables));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=utf-8");

        LOGGER.info("кто то пытается потушить сервер");
        Map<String, Object> pageVariables = new HashMap<>();


        String password = req.getParameter("password");

        if ("1489".equals(password)) {
            LOGGER.info("Shutdown server");
            try {
                server.stop();
            } catch (Exception e) {
                LOGGER.error("ошибка выключения сервера");
                LOGGER.error(e);
            }

            pageVariables.put("status", HttpServletResponse.SC_OK);
            pageVariables.put("info", "потушил");
        } else {
            pageVariables.put("status", HttpServletResponse.SC_BAD_REQUEST);
            pageVariables.put("info", "не балуйся");
        }

        try (PrintWriter pw = resp.getWriter()) {
            pw.println(PageGenerator.getPage("authresponse.txt", pageVariables));
        }
    }
}

