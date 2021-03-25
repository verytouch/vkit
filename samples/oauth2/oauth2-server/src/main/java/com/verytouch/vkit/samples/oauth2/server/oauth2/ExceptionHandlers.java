package com.verytouch.vkit.samples.oauth2.server.oauth2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ExceptionHandlers {

    public static void showMessage(HttpServletRequest request, HttpServletResponse response, Exception exception) throws IOException {
        exception.printStackTrace();
        response.setContentType("text/plain;charset=utf-8");
        response.getWriter().write(exception.getMessage());
    }

}
