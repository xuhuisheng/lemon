package com.mossle.core.util;

import java.io.UnsupportedEncodingException;

import java.net.URLEncoder;

import java.util.Enumeration;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.Assert;

/**
 * servlet utils.
 * 
 * @author Lingo
 */
public class ServletUtils {
    // -- Content Type 定义 --//
    /** text type. */
    public static final String TEXT_TYPE = "text/plain";

    /** json type. */
    public static final String JSON_TYPE = "application/json";

    /** xml type. */
    public static final String XML_TYPE = "text/xml";

    /** html type. */
    public static final String HTML_TYPE = "text/html";

    /** js type. */
    public static final String JS_TYPE = "text/javascript";

    /** excel type. */
    public static final String EXCEL_TYPE = "application/vnd.ms-excel";

    /** stream type. */
    public static final String STREAM_TYPE = "application/octet-stream";

    // -- Header 定义 --//
    /** authencation header. */
    public static final String AUTHENTICATION_HEADER = "Authorization";

    // -- 常用数值定义 --//
    /** one year seconds. */
    public static final long ONE_YEAR_SECONDS = 60 * 60 * 24 * 365;

    /** mill seconds. */
    public static final int MILL_SECONDS = 1000;

    /** protected constructor. */
    protected ServletUtils() {
    }

    /**
     * 设置客户端缓存过期时间的Header.
     * 
     * @param response
     *            HttpServletResponse
     * @param expiresSeconds
     *            long
     */
    public static void setExpiresHeader(HttpServletResponse response,
            long expiresSeconds) {
        // Http 1.0 header
        response.setDateHeader("Expires", System.currentTimeMillis()
                + (expiresSeconds * MILL_SECONDS));
        // Http 1.1 header
        response.setHeader("Cache-Control", "private, max-age="
                + expiresSeconds);
    }

    /**
     * 设置禁止客户端缓存的Header.
     * 
     * @param response
     *            HttpServletResponse
     */
    public static void setDisableCacheHeader(HttpServletResponse response) {
        // Http 1.0 header
        response.setDateHeader("Expires", 1L);
        response.addHeader("Pragma", "no-cache");
        // Http 1.1 header
        response.setHeader("Cache-Control", "no-cache, no-store, max-age=0");
    }

    /**
     * 设置LastModified Header.
     * 
     * @param response
     *            HttpServletResponse
     * @param lastModifiedDate
     *            long
     */
    public static void setLastModifiedHeader(HttpServletResponse response,
            long lastModifiedDate) {
        response.setDateHeader("Last-Modified", lastModifiedDate);
    }

    /**
     * 设置Etag Header.
     * 
     * @param response
     *            HttpServletResponse
     * @param etag
     *            String
     */
    public static void setEtag(HttpServletResponse response, String etag) {
        response.setHeader("ETag", etag);
    }

    /**
     * 根据浏览器If-Modified-Since Header, 计算文件是否已被修改.
     * 
     * 如果无修改, checkIfModify返回false ,设置304 not modify status.
     * 
     * @param request
     *            HttpServletRequest
     * @param response
     *            HttpServletResponse
     * @param lastModified
     *            内容的最后修改时间.
     * @return boolean
     */
    public static boolean checkIfModifiedSince(HttpServletRequest request,
            HttpServletResponse response, long lastModified) {
        long ifModifiedSince = request.getDateHeader("If-Modified-Since");

        if ((ifModifiedSince != -1)
                && (lastModified < (ifModifiedSince + MILL_SECONDS))) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);

            return false;
        }

        return true;
    }

    /**
     * 根据浏览器 If-None-Match Header, 计算Etag是否已无效.
     * 
     * 如果Etag有效, checkIfNoneMatch返回false, 设置304 not modify status.
     * 
     * @param request
     *            HttpServletRequest
     * @param response
     *            HttpServletResponse
     * @param etag
     *            内容的ETag.
     * @return boolean
     */
    public static boolean checkIfNoneMatchEtag(HttpServletRequest request,
            HttpServletResponse response, String etag) {
        String headerValue = request.getHeader("If-None-Match");

        if (headerValue != null) {
            boolean conditionSatisfied = false;

            if (!"*".equals(headerValue)) {
                StringTokenizer commaTokenizer = new StringTokenizer(
                        headerValue, ",");

                while (!conditionSatisfied && commaTokenizer.hasMoreTokens()) {
                    String currentToken = commaTokenizer.nextToken();

                    if (currentToken.trim().equals(etag)) {
                        conditionSatisfied = true;
                    }
                }
            } else {
                conditionSatisfied = true;
            }

            if (conditionSatisfied) {
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                response.setHeader("ETag", etag);

                return false;
            }
        }

        return true;
    }

    /**
     * 设置让浏览器弹出下载对话框的Header.
     * 
     * @param fileName
     *            下载后的文件名.
     */
    public static void setFileDownloadHeader(HttpServletRequest request,
            HttpServletResponse response, String fileName)
            throws UnsupportedEncodingException {
        // 中文文件名支持
        String encodedFileName = null;
        // 替换空格，否则firefox下有空格文件名会被截断,其他浏览器会将空格替换成+号
        encodedFileName = fileName.trim().replaceAll(" ", "_");

        String agent = request.getHeader("User-Agent");
        boolean isMSIE = ((agent != null) && (agent.toUpperCase().indexOf(
                "MSIE") != -1));

        if (isMSIE) {
            encodedFileName = URLEncoder.encode(encodedFileName, "UTF-8");
        } else {
            encodedFileName = new String(fileName.getBytes("UTF-8"),
                    "ISO8859-1");
        }

        response.setHeader("Content-Disposition", "attachment; filename=\""
                + encodedFileName + "\"");
    }

    /**
     * 取得带相同前缀的Request Parameters. 返回的结果的Parameter名已去除前缀.
     * 
     * @param request
     *            HttpServletRequest
     * @param prefix
     *            String
     * @return Map
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getParametersStartingWith(
            ServletRequest request, String prefix) {
        Assert.notNull(request, "Request must not be null");

        Enumeration paramNames = request.getParameterNames();
        Map<String, Object> params = new TreeMap<String, Object>();

        String thePrefix = (prefix == null) ? "" : prefix;

        while (paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();

            if ("".equals(thePrefix) || paramName.startsWith(thePrefix)) {
                String unprefixed = paramName.substring(thePrefix.length());
                String[] values = request.getParameterValues(paramName);

                if ((values == null) || (values.length == 0)) {
                    // Do nothing, no values found at all.
                    continue;
                }

                if (values.length > 1) {
                    params.put(unprefixed, values);
                } else {
                    params.put(unprefixed, values[0]);
                }
            }
        }

        return params;
    }

    public static Map<String, Object> getParametersStartingWith(
            Map<String, Object> parameterMap, String prefix) {
        Map<String, Object> params = new TreeMap<String, Object>();

        String thePrefix = (prefix == null) ? "" : prefix;

        for (Map.Entry<String, Object> entry : parameterMap.entrySet()) {
            String paramName = entry.getKey();
            Object paramValue = entry.getValue();

            if ("".equals(thePrefix) || paramName.startsWith(thePrefix)) {
                String unprefixed = paramName.substring(thePrefix.length());

                if (paramValue == null) {
                    // Do nothing, no values found at all.
                    continue;
                }

                params.put(unprefixed, paramValue);
            }
        }

        return params;
    }

    /**
     * 对Http Basic验证的 Header进行编码.
     * 
     * @param userName
     *            String
     * @param password
     *            String
     * @return String
     */
    public static String encodeHttpBasic(String userName, String password)
            throws UnsupportedEncodingException {
        String encode = userName + ":" + password;

        return "Basic " + EncodeUtils.base64Encode(encode.getBytes("UTF-8"));
    }
}
