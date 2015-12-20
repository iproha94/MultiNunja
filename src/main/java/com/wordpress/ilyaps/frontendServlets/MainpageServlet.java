package com.wordpress.ilyaps.frontendServlets;

import org.jetbrains.annotations.NotNull;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ilya on 26.09.15.
 */
public class MainpageServlet extends HttpServlet {
    @Override
    public void doGet(@NotNull HttpServletRequest request,
                      @NotNull HttpServletResponse response)
            throws ServletException, IOException
    {
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        String nameInSession = ServletsHelper.getNameInSession(request);

        Map<String, Object> pageVariables = new HashMap<>();
        pageVariables.put("name", nameInSession);

        ServletsHelper.mainpageInResponse(pageVariables, response);
    }
}
