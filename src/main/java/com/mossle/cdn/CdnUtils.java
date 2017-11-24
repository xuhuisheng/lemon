package com.mossle.cdn;

import java.io.File;
import java.io.FileOutputStream;

import java.net.HttpURLConnection;
import java.net.URL;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.multipart.MultipartFile;

public class CdnUtils {
    private static Logger logger = LoggerFactory.getLogger(CdnUtils.class);

    // baseDir : mossle.store/cdn
    // spaceName : public
    // targetFileName : mossle.store/cdn/public/2017/03/24/uuid.jpg
    public static String copyUrlToFile(String baseDir, String url,
            String spaceName) throws Exception {
        String targetFileName = generateTargetFileName(url);

        return copyUrlToFile(baseDir, url, spaceName, targetFileName);
    }

    public static String copyUrlToFile(String baseDir, String url,
            String spaceName, String targetFileName) throws Exception {
        if (targetFileName == null) {
            return copyUrlToFile(baseDir, url, spaceName);
        }

        if (targetFileName.indexOf("../") != -1) {
            logger.info("invalid : {}", targetFileName);
            throw new IllegalStateException("invalid : " + targetFileName);
        }

        File file = findTargetFile(baseDir, spaceName, targetFileName);

        HttpURLConnection conn = (HttpURLConnection) new URL(url)
                .openConnection();

        if (url.startsWith("https://")) {
            configHttps(conn);
        }

        FileOutputStream fos = new FileOutputStream(file);
        IOUtils.copy(conn.getInputStream(), fos);

        fos.flush();
        fos.close();

        return targetFileName;
    }

    public static String copyMultipartFileToFile(String baseDir,
            MultipartFile multipartFile, String spaceName) throws Exception {
        String targetFileName = generateTargetFileName(multipartFile
                .getOriginalFilename());

        return copyMultipartFileToFile(baseDir, multipartFile, spaceName,
                targetFileName);
    }

    public static String copyMultipartFileToFile(String baseDir,
            MultipartFile multipartFile, String spaceName, String targetFileName)
            throws Exception {
        if (targetFileName == null) {
            return copyMultipartFileToFile(baseDir, multipartFile, spaceName);
        }

        if (targetFileName.indexOf("../") != -1) {
            logger.info("invalid : {}", targetFileName);
            throw new IllegalStateException("invalid : " + targetFileName);
        }

        File file = findTargetFile(baseDir, spaceName, targetFileName);

        multipartFile.transferTo(file);

        return targetFileName;
    }

    public static String generateTargetFileName(String originalName) {
        DateFormat dateFormat = new SimpleDateFormat("/yyyy/MM/dd/");
        String targetFileName = dateFormat.format(new Date())
                + UUID.randomUUID().toString() + findSuffix(originalName);

        return targetFileName;
    }

    public static File findTargetFile(String baseDir, String spaceName,
            String targetFileName) {
        File file = new File(baseDir + "/" + spaceName, targetFileName);

        if (file.exists()) {
            logger.info("file exists : {}, overwrite", file);
        } else if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        return file;
    }

    public static String findSuffix(String url) {
        String text = url.trim();

        if (text.indexOf("?") != -1) {
            text = text.substring(0, text.indexOf("?"));
        }

        if (text.indexOf(".") == -1) {
            // no suffix
            return "";
        }

        text = text.substring(text.lastIndexOf("."));

        // suffix cannot contains un-normal char
        return text.trim().toLowerCase();
    }

    public static void configHttps(HttpURLConnection conn) throws Exception {
        TrustManager[] tm = { new TrustAllX509TrustManager() };
        SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");

        sslContext.init(null, tm, new java.security.SecureRandom());

        SSLSocketFactory ssf = sslContext.getSocketFactory();

        // ssf.setHostnameVerifier(new TrustAllHostnameVerifier());
        ((HttpsURLConnection) conn).setSSLSocketFactory(ssf);
        ((HttpsURLConnection) conn)
                .setHostnameVerifier(new TrustAllHostnameVerifier());
    }
}
