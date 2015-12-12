package com.wordpress.ilyaps.frontendServlets;

import com.wordpress.ilyaps.ThreadSettings;
import com.wordpress.ilyaps.frontendService.FrontendService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import com.wordpress.ilyaps.utils.PageGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by v.chibrikov on 13.09.2014.
 */
public class RegisterServlet extends HttpServlet {
    private static final int STATUSTEAPOT = 418;

    @NotNull
    static final Logger LOGGER = LogManager.getLogger(RegisterServlet.class);
    @NotNull
    private FrontendService feService;

    public RegisterServlet(@NotNull FrontendService feService) {
        this.feService = feService;
    }

    @Override
    public void doGet(@NotNull HttpServletRequest request,
                      @NotNull HttpServletResponse response)
            throws ServletException, IOException
    {
        response.setCharacterEncoding("utf-8");
        Map<String, Object> pageVariables = new HashMap<>();

        if (feService.isAuthorized(request.getSession().getId())) {
            LOGGER.info("пользователь уже авторизован");
            pageVariables.put("status", STATUSTEAPOT);
            pageVariables.put("info", "ты уже авторизован");
        } else {
            LOGGER.info("пользователь хочет зарегаться");
        }

        if (feService.isAuthorized(request.getSession().getId())) {
            response.getWriter().println(PageGenerator.getPage("authresponse.txt", pageVariables));
        } else {
            response.getWriter().println(PageGenerator.getPage("auth/signup.html", pageVariables));
        }
    }

    @Override
    public void doPost(@NotNull HttpServletRequest request,
                       @NotNull HttpServletResponse response)
            throws ServletException, IOException
    {
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        Map<String, Object> pageVariables = new HashMap<>();

        if (name == null || password == null || email == null) {
            LOGGER.info("имя, пароль или емейл - пусты");
            pageVariables.put("status", HttpServletResponse.SC_BAD_REQUEST);
            pageVariables.put("info", "имя, пароль или емейл - пусты");
        } else if (name.length() < 4 || email.length() < 4) {
            LOGGER.info("Короткое имя или емэйл");
            pageVariables.put("status", HttpServletResponse.SC_BAD_REQUEST);
            pageVariables.put("info", "Короткое имя или емэйл");
        } else if (Pattern.matches(".*[А-Яа-я]+.*", name) || Pattern.matches(".*[А-Яа-я]+.*", email)) {
            LOGGER.info("Я криворукий и не сделал хранение в бд русских букв");
            pageVariables.put("status", HttpServletResponse.SC_BAD_REQUEST);
            pageVariables.put("info", "Я криворукий и не сделал хранение в бд русских букв, но виноват все равно ты");
        } else {
            LOGGER.info("начинаем регистрацию");
            feService.register(name, email, password);

            while (!feService.endedRegistration(email)) {
                LOGGER.info("ждем окончание регистрации");
                try {
                    synchronized (this) {
                        this.wait(ThreadSettings.SLEEP_TIME);
                    }
                } catch (InterruptedException e) {
                    LOGGER.error("wait потока сервлета");
                    LOGGER.error(e);
                }
            }

            if (feService.successfulRegistration(email)) {
                LOGGER.info("регистрация успешно пройдена");
                pageVariables.put("status", HttpServletResponse.SC_OK);
                pageVariables.put("info", "спасибо, за регистрацию");
            } else {
                LOGGER.warn("либо фигня на сервере, либо такой пользователь уже зареган");
                pageVariables.put("status", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                pageVariables.put("info", "либо фигня на сервере, либо такой пользователь уже зареган");
            }

        }

        response.getWriter().println(PageGenerator.getPage("authresponse.txt", pageVariables));
    }
}
