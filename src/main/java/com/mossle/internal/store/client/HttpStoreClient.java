package com.mossle.internal.store.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import java.util.Map;

import com.mossle.api.store.StoreDTO;

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.store.InputStreamDataSource;
import com.mossle.core.util.IoUtils;

import org.apache.commons.codec.binary.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

public class HttpStoreClient implements StoreClient {
    private Logger logger = LoggerFactory.getLogger(HttpStoreClient.class);
    private String baseUrl;
    private String model;

    public StoreDTO saveStore(InputStream inputStream, String fileName,
            String contentType, String tenantId) throws Exception {
        URL url = new URL(baseUrl + "/saveStore");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setDoInput(true);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IoUtils.copyStream(inputStream, baos);

        String content = new String(new Base64().encodeBase64(baos
                .toByteArray()));

        String queryString = "model=" + model + "&fileName=" + fileName
                + "&contentType=" + URLEncoder.encode(contentType, "utf-8")
                + "&content=" + URLEncoder.encode(content, "utf-8")
                + "&tenantId=" + URLEncoder.encode(tenantId, "utf-8");
        logger.debug("queryString : {}", queryString);
        conn.getOutputStream().write(queryString.getBytes("utf-8"));
        conn.getOutputStream().flush();

        InputStream is = conn.getInputStream();
        int len = -1;
        byte[] b = new byte[1024];

        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();

        while ((len = is.read(b, 0, 1024)) != -1) {
            baos2.write(b, 0, len);
        }

        is.close();

        String json = new String(baos2.toByteArray());
        Map<String, String> map = new JsonMapper().fromJson(json, Map.class);

        StoreDTO storeDto = new StoreDTO();
        storeDto.setModel(model);
        storeDto.setKey(map.get("data"));
        storeDto.setDataSource(new InputStreamDataSource(inputStream));

        return storeDto;
    }

    public StoreDTO getStore(String key, String tenantId) throws Exception {
        String queryString = "model=" + model + "&key=" + key + "&tenantId="
                + tenantId;
        URL url = new URL(baseUrl + "/getStore?" + queryString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setDoInput(true);

        logger.debug("queryString : {}", queryString);

        InputStream is = conn.getInputStream();
        int len = -1;
        byte[] b = new byte[1024];
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();

        while ((len = is.read(b, 0, 1024)) != -1) {
            baos2.write(b, 0, len);
        }

        is.close();

        String json = new String(baos2.toByteArray());
        Map<String, String> map = new JsonMapper().fromJson(json, Map.class);
        String base64 = map.get("data");
        logger.debug(base64);

        byte[] b2 = new Base64().decodeBase64(base64.getBytes());
        StoreDTO storeDto = new StoreDTO();
        storeDto.setModel(model);
        storeDto.setKey(key);
        storeDto.setDataSource(new InputStreamDataSource(
                new ByteArrayInputStream(b2)));

        return storeDto;
    }

    @Value("${store.baseUrl}")
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Value("${store.model}")
    public void setModel(String model) {
        this.model = model;
    }
}
