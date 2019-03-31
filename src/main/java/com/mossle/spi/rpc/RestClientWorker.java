package com.mossle.spi.rpc;

import java.io.InputStream;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.FormParam;
import javax.ws.rs.Path;

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.util.BaseDTO;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestClientWorker implements Runnable {
    private static Logger logger = LoggerFactory
            .getLogger(RestClientWorker.class);
    private JsonMapper jsonMapper = new JsonMapper();
    private String baseUrl;
    private String accessKey;
    private String accessSecret;
    private RpcAuthHelper rpcAuthHelper;
    private Method method;
    private Object[] args;

    public RestClientWorker(String baseUrl, String accessKey,
            String accessSecret, RpcAuthHelper rpcAuthHelper, Method method,
            Object[] args) {
        this.baseUrl = baseUrl;
        this.accessKey = accessKey;
        this.accessSecret = accessSecret;
        this.rpcAuthHelper = rpcAuthHelper;
        this.method = method;
        this.args = args;
    }

    public void run() {
        try {
            this.execute();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void execute() throws Exception {
        String path = this.processPath(method);
        String httpMethod = this.processMethod(method);
        String body = this.processBody(method, args);

        try {
            String url = baseUrl + path;
            logger.info("url : {}", url);
            logger.info("body : {}", body);

            HttpURLConnection conn = (HttpURLConnection) new URL(url)
                    .openConnection();
            conn.setConnectTimeout(1000);
            conn.setReadTimeout(1000);
            conn.setRequestMethod(httpMethod);

            // String signature = Signer.sign(accessKey, accessSecret, body);
            // String signature = rpcAuthHelper.calculateSignature();
            // String authorization = "m " + accessKey + ":" + signature;
            RequestConfig requestConfig = new RequestConfig();
            requestConfig.setMethod(httpMethod);
            requestConfig.setPath(path);
            requestConfig.setQueryString("");
            requestConfig.setPayload(body);
            requestConfig.setAccessKey(accessKey);
            requestConfig.setAccessSecret(accessSecret);

            String authorization = rpcAuthHelper
                    .generateAuthorization(requestConfig);
            // conn.addRequestProperty("Content-MD5", contentMd5);
            conn.addRequestProperty("Authorization", authorization);
            conn.addRequestProperty("Date", "Thu, 11 Jul 2015 15:33:24 GMT");
            conn.addRequestProperty("x-request-id", "1");
            conn.setDoOutput(true);
            conn.getOutputStream().write(body.getBytes("UTF-8"));

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // InputStream is = conn.getInputStream();
                // return IoUtils.readString(is, encoding);
                InputStream is = conn.getInputStream();
                String resultText = IOUtils.toString(is, "UTF-8");
                logger.info("resultText : {}", resultText);

                BaseDTO result = jsonMapper.fromJson(resultText, BaseDTO.class);

                return;
            } else {
                InputStream is = conn.getErrorStream();

                // String text = IoUtils.readString(is, encoding);
                String text = IOUtils.toString(is, "UTF-8");
                logger.error("error : {} {}", conn.getResponseCode(), text);

                // throw new IllegalStateException(conn.getResponseMessage());
                BaseDTO result = new BaseDTO();
                result.setCode(500);
                result.setMessage(conn.getResponseMessage());

                return;
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            BaseDTO result = new BaseDTO();
            result.setCode(500);
            result.setMessage(ex.getMessage());

            return;
        }
    }

    public String processPath(Method method) {
        StringBuilder buff = new StringBuilder();
        Path classPath = method.getDeclaringClass().getAnnotation(Path.class);

        if (classPath != null) {
            buff.append("/").append(classPath.value());
        }

        Path methodPath = method.getAnnotation(Path.class);

        if (methodPath != null) {
            buff.append("/").append(methodPath.value());
        }

        return buff.toString();
    }

    public String processMethod(Method method) {
        return "POST";
    }

    public String processBody(Method method, Object[] args) throws Exception {
        StringBuilder buff = new StringBuilder();

        for (int i = 0; i < args.length; i++) {
            String name = this.findParameterName(method
                    .getParameterAnnotations()[i]);

            if (name == null) {
                continue;
            }

            Object value = args[i];
            String textValue = this.findParameterValue(value);
            buff.append("&").append(name).append("=").append(textValue);
        }

        if (buff.length() > 1) {
            buff.deleteCharAt(0);
        }

        return buff.toString();
    }

    public String findParameterName(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof FormParam) {
                return ((FormParam) annotation).value();
            }
        }

        return null;
    }

    public String findParameterValue(Object value) throws Exception {
        if (value == null) {
            return null;
        }

        if (value.getClass().isPrimitive()) {
            return value.toString();
        }

        if (value instanceof String) {
            return (String) value;
        }

        return jsonMapper.toJson(value);
    }
}
