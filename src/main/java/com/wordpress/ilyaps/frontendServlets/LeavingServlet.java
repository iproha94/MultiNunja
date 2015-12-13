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
 * Created by ilya on 26.09.15.
 */
public class LeavingServlet extends HttpServlet {
    private static final int STATUSTEAPOT = 418;

    @NotNull
    static final Logger LOGGER = LogManager.getLogger(RegisterServlet.class);
    @NotNull
    private FrontendService feService;

    public LeavingServlet(@NotNull FrontendService feService) {
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

        if (request.getSession().getAttribute("name") == null ||
                "Incognitto".equals(request.getSession().getAttribute("name"))) {
            LOGGER.info("куда пошел? ты еще не авторизовался");
            pageVariables.put("status", STATUSTEAPOT);
            pageVariables.put("info", "ты еще не авторизован");
        } else {
            LOGGER.info("пользователь хочет выйти");
            LOGGER.info("начинаем leaving");
            feService.leaving(request.getSession().getId());

            while (!feService.endedLeaving(request.getSession().getId())) {
                LOGGER.info("ждем окончание leaving");
                try {
                    synchronized (this) {
                        this.wait(ThreadSettings.SLEEP_TIME_SERVLET);
                    }
                } catch (InterruptedException e) {
                    LOGGER.error("wait потока сервлета");
                    LOGGER.error(e);
                }
            }

            UserProfile profile = feService.successfulLeaving(request.getSession().getId());
            if (profile != null) {
                LOGGER.info("leaving успешно пройдена");
                request.getSession().setAttribute("name", "Incognitto");
                pageVariables.put("status", HttpServletResponse.SC_OK);
                pageVariables.put("info", "возвращайся скорее");
            } else {
                LOGGER.warn("фигня на сервере");
                pageVariables.put("status", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                pageVariables.put("info", "фигня на сервере");
            }
        }

        try (PrintWriter pw = response.getWriter()) {
            pw.println(PageGenerator.getPage("authresponse.txt", pageVariables));
        }

    }
}
