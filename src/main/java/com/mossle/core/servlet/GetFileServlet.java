package com.mossle.core.servlet;

import java.io.IOException;

import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.core.util.IoUtils;

public class GetFileServlet extends HttpServlet {
    public static final int DEBAULT_BUFFER_SIZE = 1024;
    private static final long serialVersionUID = 0L;
    private String baseDir = "/home/ckfinder/userfiles/";

    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        String requestUri = request.getRequestURI();
        requestUri = requestUri.substring(request.getContextPath().length());
        requestUri = requestUri.substring("/userfiles".length());

        String fileName = baseDir + URLDecoder.decode(requestUri, "UTF-8");
        IoUtils.copyFileToOutputStream(fileName, response.getOutputStream());
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }
}
