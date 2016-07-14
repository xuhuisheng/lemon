package com.mossle.core.servlet;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import java.util.zip.GZIPOutputStream;

import javax.activation.MimetypesFileTypeMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.core.util.ServletUtils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class StaticContentFilter implements Filter {
    /** 需要被Gzip压缩的Mime类型. */
    public static final String[] GZIP_MIME_TYPES = { "text/html",
            "application/xhtml+xml", "text/plain", "text/css",
            "text/javascript", "application/x-javascript", "application/json" };

    /** 需要被Gzip压缩的最小文件大小. */
    public static final int GZIP_MINI_LENGTH = 512;
    private MimetypesFileTypeMap mimetypesFileTypeMap;
    private long expiresSeconds;
    private FilterConfig filterConfig;

    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        // 初始化mimeTypes, 默认缺少css的定义,添加之.
        mimetypesFileTypeMap = new MimetypesFileTypeMap();
        mimetypesFileTypeMap.addMimeTypes("text/css css");
    }

    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse res,
            FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // 获取请求内容的基本信息.
        String requestUri = request.getRequestURI();
        String contentPath = requestUri.substring(request.getContextPath()
                .length());

        ContentInfo contentInfo = getContentInfo(contentPath);

        if (contentInfo.getFile().isDirectory()) {
            if (requestUri.endsWith("/")) {
                response.sendRedirect(requestUri + "index.html");
            } else {
                response.sendRedirect(requestUri + "/index.html");
            }

            return;
        }

        if (!contentInfo.getFile().exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);

            return;
        }

        // 根据Etag或ModifiedSince Header判断客户端的缓存文件是否有效, 如仍有效则设置返回码为304,直接返回.
        if (!ServletUtils.checkIfModifiedSince(request, response,
                contentInfo.getLastModified())
                || !ServletUtils.checkIfNoneMatchEtag(request, response,
                        contentInfo.getEtag())) {
            return;
        }

        // 设置Etag/过期时间
        ServletUtils.setExpiresHeader(response, ServletUtils.ONE_YEAR_SECONDS);
        ServletUtils.setLastModifiedHeader(response,
                contentInfo.getLastModified());
        ServletUtils.setEtag(response, contentInfo.getEtag());

        // 设置MIME类型
        response.setContentType(contentInfo.getMimeType());

        // 设置弹出下载文件请求窗口的Header
        if (request.getParameter("download") != null) {
            ServletUtils.setFileDownloadHeader(request, response,
                    contentInfo.getFileName());
        }

        // 构造OutputStream
        OutputStream output;

        // if (checkAccetptGzip(request) && contentInfo.isNeedGzip()) {
        // 使用压缩传输的outputstream, 使用http1.1 chunked编码不设置content-length.
        // output = buildGzipOutputStream(response);
        // } else {
        // 使用普通outputstream, 设置content-length.
        response.setContentLength(contentInfo.length);
        output = response.getOutputStream();
        // }

        // 高效读取文件内容并输出,然后关闭input file
        FileUtils.copyFile(contentInfo.getFile(), output);
        output.flush();
    }

    /**
     * 检查浏览器客户端是否支持gzip编码.
     */
    private static boolean checkAccetptGzip(HttpServletRequest request) {
        // Http1.1 header
        String acceptEncoding = request.getHeader("Accept-Encoding");

        return StringUtils.contains(acceptEncoding, "gzip");
    }

    /**
     * 设置Gzip Header并返回GZIPOutputStream.
     */
    private OutputStream buildGzipOutputStream(HttpServletResponse response)
            throws IOException {
        response.setHeader("Content-Encoding", "gzip");
        response.setHeader("Vary", "Accept-Encoding");

        return new GZIPOutputStream(response.getOutputStream());
    }

    /**
     * 创建Content基本信息.
     */
    private ContentInfo getContentInfo(String contentPath) {
        ContentInfo contentInfo = new ContentInfo();

        String realFilePath = filterConfig.getServletContext().getRealPath(
                contentPath);
        File file = new File(realFilePath);

        contentInfo.setFile(file);
        contentInfo.setContentPath(contentPath);
        contentInfo.setFileName(file.getName());
        contentInfo.setLength((int) file.length());

        contentInfo.setLastModified(file.lastModified());
        contentInfo.setEtag("W/\"" + contentInfo.lastModified + "\"");

        contentInfo.setMimeType(mimetypesFileTypeMap
                .getContentType(contentInfo.fileName));

        if ((contentInfo.length >= GZIP_MINI_LENGTH)
                && ArrayUtils.contains(GZIP_MIME_TYPES,
                        contentInfo.getMimeType())) {
            contentInfo.setNeedGzip(true);
        } else {
            contentInfo.setNeedGzip(false);
        }

        return contentInfo;
    }

    public void setExpiresSeconds(long expiresSeconds) {
        this.expiresSeconds = expiresSeconds;
    }

    static class ContentInfo {
        private String contentPath;
        private File file;
        private String fileName;
        private int length;
        private String mimeType;
        private long lastModified;
        private String etag;
        private boolean needGzip;

        public String getContentPath() {
            return this.contentPath;
        }

        public void setContentPath(String contentPath) {
            this.contentPath = contentPath;
        }

        public File getFile() {
            return this.file;
        }

        public void setFile(File file) {
            this.file = file;
        }

        public String getFileName() {
            return this.fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public int getLength() {
            return this.length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public String getMimeType() {
            return this.mimeType;
        }

        public void setMimeType(String mimeType) {
            this.mimeType = mimeType;
        }

        public long getLastModified() {
            return this.lastModified;
        }

        public void setLastModified(long lastModified) {
            this.lastModified = lastModified;
        }

        public String getEtag() {
            return this.etag;
        }

        public void setEtag(String etag) {
            this.etag = etag;
        }

        public boolean isNeedGzip() {
            return this.needGzip;
        }

        public void setNeedGzip(boolean needGzip) {
            this.needGzip = needGzip;
        }
    }
}
