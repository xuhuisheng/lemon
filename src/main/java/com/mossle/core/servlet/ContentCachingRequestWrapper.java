package com.mossle.core.servlet;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import java.net.URLDecoder;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

// import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public class ContentCachingRequestWrapper extends HttpServletRequestWrapper {
    private byte[] body;
    private BufferedReader reader;
    private ServletInputStream inputStream;
    private Map<String, String[]> parameterMap;
    private String queryString;

    public ContentCachingRequestWrapper(HttpServletRequest request)
            throws IOException {
        super(request);
        loadBody(request);
    }

    private void loadBody(HttpServletRequest request) throws IOException {
        body = IOUtils.toByteArray(request.getInputStream());
        queryString = request.getQueryString();
        inputStream = new RequestCachingInputStream(body);
        this.loadParameterMap();
    }

    public byte[] getBody() {
        return body;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (inputStream != null) {
            return inputStream;
        }

        return super.getInputStream();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        if (reader == null) {
            reader = new BufferedReader(new InputStreamReader(inputStream,
                    getCharacterEncoding()));
        }

        return reader;
    }

    @Override
    public String getParameter(String name) {
        // System.out.println("getParameter " + name);
        String[] values = parameterMap.get(name);

        if (values == null) {
            return null;
        }

        if (values.length < 1) {
            return null;
        }

        return values[0];
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        // System.out.println("getParameterMap ");
        return parameterMap;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        // System.out.println("getParameterNames ");
        Vector<String> vector = new Vector(parameterMap.keySet());

        return vector.elements();
    }

    @Override
    public String[] getParameterValues(String name) {
        // System.out.println("getParameterValues ");
        return parameterMap.get(name);
    }

    public void loadParameterMap() throws UnsupportedEncodingException {
        String parameterText = new String(body, getCharacterEncoding());
        parameterText = URLDecoder.decode(parameterText, "UTF-8");

        if (StringUtils.isNotBlank(queryString)) {
            parameterText += ("&" + queryString);
        }

        String[] parameterArray = parameterText.split("&");
        Map<String, List<String>> parameterListMap = new HashMap<String, List<String>>();

        for (String parameterPair : parameterArray) {
            String[] array = parameterPair.split("=");

            if (array.length == 0) {
                // System.out.println(parameterPair);
                continue;
            }

            if (array.length == 1) {
                String name = array[0];
                array = new String[2];
                array[0] = name;
                array[1] = "";
            }

            String name = array[0];
            String value = array[1];

            if (parameterListMap.containsKey(name)) {
                List<String> values = parameterListMap.get(name);
                values.add(value);
            } else {
                List<String> values = new ArrayList<String>();
                parameterListMap.put(name, values);
                values.add(value);
            }
        }

        // System.out.println(parameterListMap);
        String[] strArray = new String[0];
        parameterMap = new HashMap<String, String[]>();

        for (Map.Entry<String, List<String>> entry : parameterListMap
                .entrySet()) {
            parameterMap
                    .put(entry.getKey(), entry.getValue().toArray(strArray));
        }
    }

    private static class RequestCachingInputStream extends ServletInputStream {
        private final ByteArrayInputStream inputStream;

        public RequestCachingInputStream(byte[] bytes) {
            inputStream = new ByteArrayInputStream(bytes);
        }

        @Override
        public int read() throws IOException {
            return inputStream.read();
        }

        // @Override
        // public boolean isFinished() {
        // return inputStream.available() == 0;
        // }

        // @Override
        // public boolean isReady() {
        // return true;
        // }

        // @Override
        // public void setReadListener(ReadListener readlistener) {
        // }
    }
}
