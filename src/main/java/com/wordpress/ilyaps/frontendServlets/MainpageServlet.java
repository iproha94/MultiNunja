package com.wordpress.ilyaps.frontendServlets;

import com.wordpress.ilyaps.utils.PageGenerator;
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
public class MainpageServlet extends HttpServlet {
    @Override
    public void doGet(@NotNull HttpServletRequest request,
                      @NotNull HttpServletResponse response)
            throws ServletException, IOException
    {
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        if (request.getSession().getAttribute("name") == null) {
            request.getSession().setAttribute("name", "Incognitto");
        }

        Map<String, Object> pageVariables = new HashMap<>();
        pageVariables.put("name", request.getSession().getAttribute("name"));

        try (PrintWriter pw = response.getWriter()) {
            pw.println(PageGenerator.getPage("mainpage.html", pageVariables));
        }
    }
}
