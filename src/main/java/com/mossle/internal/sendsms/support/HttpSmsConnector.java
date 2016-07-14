package com.mossle.internal.sendsms.support;

import java.io.*;

import java.net.*;

import java.util.HashMap;
import java.util.Map;

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.util.Md5Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpSmsConnector implements SmsConnector {
    private static Logger logger = LoggerFactory
            .getLogger(HttpSmsConnector.class);
    public static final Integer STATUS_SUCCESS = Integer.valueOf(200);
    private JsonMapper jsonMapper = new JsonMapper();

    public SmsDTO send(SmsDTO smsDto, SmsServerInfo smsServerInfo) {
        try {
            String mobile = smsDto.getMobile();
            String message = smsDto.getMessage();
            logger.info("mobile : {}", mobile);
            logger.info("message : {}", message);

            String url = smsServerInfo.getHost();

            HttpURLConnection conn = (HttpURLConnection) new URL(url)
                    .openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);

            String mobileFieldName = smsServerInfo.getMobileFieldName();
            String messageFieldName = smsServerInfo.getMessageFieldName();

            String queryString = mobileFieldName + "=" + mobile + "&"
                    + messageFieldName + "=" + message;

            conn.getOutputStream().write(queryString.getBytes("UTF-8"));
            conn.getOutputStream().flush();

            InputStream is = conn.getInputStream();
            int len = -1;
            byte[] b = new byte[1024];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            while ((len = is.read(b, 0, 1024)) != -1) {
                baos.write(b, 0, len);
            }

            is.close();

            String text = new String(baos.toByteArray(), "UTF-8");
            smsDto.setResult(text);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            smsDto.setSuccess(false);
            smsDto.setThrowable(ex);
        }

        return smsDto;
    }
}
