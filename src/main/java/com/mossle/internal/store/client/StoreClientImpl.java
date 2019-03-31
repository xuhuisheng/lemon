package com.mossle.internal.store.client;

import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.Map;

import javax.activation.DataSource;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.api.store.StoreDTO;

import com.mossle.client.store.StoreClient;

import com.mossle.core.store.InputStreamDataSource;
import com.mossle.core.util.BaseDTO;

import com.mossle.spi.rpc.RequestConfig;
import com.mossle.spi.rpc.RpcAuthHelper;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

public class StoreClientImpl implements StoreClient {
    private static Logger logger = LoggerFactory
            .getLogger(StoreClientImpl.class);
    private String baseUrl = "http://localhost:8080/mossle-web-svc";
    private String path = "/store/rs";
    private String accessKey = "lemon";
    private String accessSecret = "lemon@2018";
    private RpcAuthHelper rpcAuthHelper;

    // get object
    public StoreDTO getStore(String budgetName, String objectName,
            String tenantId) throws Exception {
        String url = baseUrl + path + "/" + objectName;

        // String url = "http://localhost:8080/mossle-web-svc/store/rs/test.txt";
        HttpURLConnection conn = (HttpURLConnection) new URL(url)
                .openConnection();
        conn.setConnectTimeout(1000);
        conn.setReadTimeout(1000);

        RequestConfig requestConfig = new RequestConfig();
        requestConfig.setMethod("GET");
        requestConfig.setPath(path + "/" + objectName);
        requestConfig.setAccessKey(accessKey);
        requestConfig.setAccessSecret(accessSecret);
        conn.addRequestProperty("authorization",
                rpcAuthHelper.generateAuthorizationStore(requestConfig));
        logger.info(conn.getResponseCode() + " " + conn.getResponseMessage());

        InputStream is = null;

        if (conn.getResponseCode() == 200) {
            is = conn.getInputStream();

            // IOUtils.copy(is, new FileOutputStream("target/test.txt"));
            StoreDTO storeDto = new StoreDTO();
            storeDto.setDataSource(new InputStreamDataSource(is));

            return storeDto;
        } else {
            is = conn.getErrorStream();

            String result = IOUtils.toString(is, "UTF-8");
            logger.info("result : {}", result);

            return null;
        }
    }

    // post object and generate object name
    public StoreDTO saveStore(String budgetName, DataSource dataSource,
            String tenantId) throws Exception {
        String url = baseUrl + path;
        HttpURLConnection conn = (HttpURLConnection) new URL(url)
                .openConnection();
        conn.setConnectTimeout(1000);
        conn.setReadTimeout(1000);

        conn.setRequestMethod("POST");

        RequestConfig requestConfig = new RequestConfig();
        requestConfig.setMethod("POST");
        requestConfig.setPath(path);
        requestConfig.setAccessKey(accessKey);
        requestConfig.setAccessSecret(accessSecret);
        conn.addRequestProperty("authorization",
                rpcAuthHelper.generateAuthorizationStore(requestConfig));
        conn.setDoOutput(true);
        IOUtils.copy(dataSource.getInputStream(), conn.getOutputStream());

        logger.info(conn.getResponseCode() + " " + conn.getResponseMessage());

        InputStream is = null;

        if (conn.getResponseCode() == 200) {
            is = conn.getInputStream();
        } else {
            is = conn.getErrorStream();
        }

        String result = IOUtils.toString(is, "UTF-8");

        logger.info("result : {}", result);

        return null;
    }

    // put object and use object name
    public StoreDTO saveStore(String budgetName, String objectName,
            DataSource dataSource, String tenantId) throws Exception {
        String url = baseUrl + path + "/" + objectName;
        HttpURLConnection conn = (HttpURLConnection) new URL(url)
                .openConnection();
        conn.setConnectTimeout(1000);
        conn.setReadTimeout(1000);

        conn.setRequestMethod("PUT");

        RequestConfig requestConfig = new RequestConfig();
        requestConfig.setMethod("PUT");
        requestConfig.setPath(path + "/" + objectName);
        requestConfig.setAccessKey(accessKey);
        requestConfig.setAccessSecret(accessSecret);
        conn.addRequestProperty("authorization",
                rpcAuthHelper.generateAuthorizationStore(requestConfig));
        conn.setDoOutput(true);
        IOUtils.copy(dataSource.getInputStream(), conn.getOutputStream());

        logger.info(conn.getResponseCode() + " " + conn.getResponseMessage());

        InputStream is = null;

        if (conn.getResponseCode() == 200) {
            is = conn.getInputStream();
        } else {
            is = conn.getErrorStream();
        }

        String result = IOUtils.toString(is, "UTF-8");

        logger.info("result : {}", result);

        return null;
    }

    // delete object
    public void removeStore(String budgetName, String objectName,
            String tenantId) throws Exception {
        String url = baseUrl + path + "/" + objectName;
        HttpURLConnection conn = (HttpURLConnection) new URL(url)
                .openConnection();
        conn.setConnectTimeout(1000);
        conn.setReadTimeout(1000);

        conn.setRequestMethod("DELETE");

        RequestConfig requestConfig = new RequestConfig();
        requestConfig.setMethod("DELETE");
        requestConfig.setPath(path + "/" + objectName);
        requestConfig.setAccessKey(accessKey);
        requestConfig.setAccessSecret(accessSecret);
        conn.addRequestProperty("authorization",
                rpcAuthHelper.generateAuthorizationStore(requestConfig));

        logger.info(conn.getResponseCode() + " " + conn.getResponseMessage());

        InputStream is = null;

        if (conn.getResponseCode() == 200) {
            is = conn.getInputStream();
        } else {
            is = conn.getErrorStream();
        }

        String result = IOUtils.toString(is, "UTF-8");

        logger.info("result : {}", result);
    }

    @Resource
    public void setRpcAuthHelper(RpcAuthHelper rpcAuthHelper) {
        this.rpcAuthHelper = rpcAuthHelper;
    }

    @Value("${client.store.baseUrl}")
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Value("${client.store.accessKey}")
    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    @Value("${client.store.accessSecret}")
    public void setAccessSecret(String accessSecret) {
        this.accessSecret = accessSecret;
    }
}
