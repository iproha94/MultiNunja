package com.wordpress.ilyaps.frontendServlets;

import com.wordpress.ilyaps.ThreadSettings;
import com.wordpress.ilyaps.accountService.UserProfile;
import com.wordpress.ilyaps.frontendService.FrontendService;
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
 * @author v.chibrikov
 */
public class AuthorizationServlet extends HttpServlet {
    private static final int STATUSTEAPOT = 418;

    @NotNull
    static final Logger LOGGER = LogManager.getLogger(RegisterServlet.class);
    @NotNull
    private FrontendService feService;

    public AuthorizationServlet(@NotNull FrontendService feService) {
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

        if (request.getSession().getAttribute("name") != null &&
                !"Incognitto".equals(request.getSession().getAttribute("name"))) {
            LOGGER.info("пользователь уже авторизован");
            pageVariables.put("status", STATUSTEAPOT);
            pageVariables.put("info", "ты уже авторизован");
        } else {
            LOGGER.info("пользователь хочет авторизоваться");
        }

        try (PrintWriter pw = response.getWriter()) {
            if (request.getSession().getAttribute("name") != null &&
                    !"Incognitto".equals(request.getSession().getAttribute("name"))) {
                pw.println(PageGenerator.getPage("authresponse.txt", pageVariables));
            } else {
                pw.println(PageGenerator.getPage("auth/signin.html", pageVariables));
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

        Map<String, Object> pageVariables = new HashMap<>();

        if (password == null || email == null) {
            LOGGER.info("имя, пароль или емейл - пусты");
            pageVariables.put("status", HttpServletResponse.SC_BAD_REQUEST);
            pageVariables.put("info", "пароль или емейл - пусты");
        } else if (request.getSession().getAttribute("name") != null &&
                !"Incognitto".equals(request.getSession().getAttribute("name"))) {
            LOGGER.info("пользователь уже авторизован");
            pageVariables.put("status", STATUSTEAPOT);
            pageVariables.put("info", "ты уже авторизован");
        } else {
            LOGGER.info("начинаем authorization");
            feService.authorization(request.getSession().getId(), email, password);

            while (!feService.endedAuthorization(request.getSession().getId())) {
                LOGGER.info("ждем окончание authorization");
                try {
                    synchronized (this) {
                        this.wait(ThreadSettings.SLEEP_TIME_SERVLET);
                    }
                } catch (InterruptedException e) {
                    LOGGER.error("wait потока сервлета");
                    LOGGER.error(e);
                }
            }

            UserProfile profile = feService.successfulAuthorization(request.getSession().getId());
            if (profile != null) {
                LOGGER.info("authorization успешно пройдена");
                request.getSession().setAttribute("name", profile.getName());
                pageVariables.put("status", HttpServletResponse.SC_OK);
                pageVariables.put("info", "спасибо, за authorization");
            } else {
                LOGGER.warn("либо фигня на сервере, либо такой пользователь не найден");
                pageVariables.put("status", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                pageVariables.put("info", "либо фигня на сервере, либо такой пользователь не найден");
            }
        }

        try (PrintWriter pw = response.getWriter()) {
            pw.println(PageGenerator.getPage("authresponse.txt", pageVariables));
        }
    }
}
