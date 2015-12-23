package com.wordpress.ilyaps.frontendServlets;

import com.wordpress.ilyaps.utils.PageGenerator;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ilya on 20.12.15.
 */
public class ServletsHelper {
    public static final int STATUSTEAPOT = 418;

    public static final String INCOGNITTO = "INCOGNITTO";
    public static final String CYRILLIC_PATTERN = ".*[А-Яа-я]+.*";

    @NotNull
    public static String getNameInSession(@NotNull HttpServletRequest request) {
        String name = (String) request.getSession().getAttribute("name");
        if (name == null) {
            return INCOGNITTO;
        }

        return name;
    }

    public static boolean nameEqualsIncognitto(String name) {
        return INCOGNITTO.equals(name);
    }

    public static void printInResponse(@NotNull Map<String, Object> pageVariables, @NotNull HttpServletResponse response) {
        try (PrintWriter pw = response.getWriter()) {
            pw.println(PageGenerator.getPage("authresponse.txt", pageVariables));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void signinInResponse(@NotNull HttpServletResponse response) {
        try (PrintWriter pw = response.getWriter()) {
            pw.println(PageGenerator.getPage("auth/signin.html", new HashMap<>()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void fastauthInResponse(@NotNull HttpServletResponse response) {
        try (PrintWriter pw = response.getWriter()) {
            pw.println(PageGenerator.getPage("auth/fastauth.html", new HashMap<>()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void signupInResponse(@NotNull HttpServletResponse response) {
        try (PrintWriter pw = response.getWriter()) {
            pw.println(PageGenerator.getPage("auth/signup.html", new HashMap<>()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void mainpageInResponse(@NotNull Map<String, Object> pageVariables, @NotNull HttpServletResponse response) {
        try (PrintWriter pw = response.getWriter()) {
            pw.println(PageGenerator.getPage("mainpage.html", pageVariables));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
