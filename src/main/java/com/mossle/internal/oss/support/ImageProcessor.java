package com.mossle.internal.oss.support;

import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageProcessor {
    private static Logger logger = LoggerFactory
            .getLogger(ImageProcessor.class);

    // x-oss-process=image/resize,w_300
    public void process(InputStream is, OutputStream os, String xOssProcess)
            throws Exception {
        int width = 0;

        String[] operationArray = xOssProcess.split("/");

        for (String operation : operationArray) {
            String[] paramArray = operation.split(",");
            String operationName = paramArray[0];

            for (int i = 1; i < paramArray.length; i++) {
                String[] param = paramArray[i].split("_");
                String paramName = param[0];
                String paramValue = param[1];
                logger.info("param : {} {}", paramName, paramValue);

                if ("w".equals(paramName)) {
                    width = Integer.parseInt(paramValue);
                }
            }
        }

        ImageUtils.zoomImage(is, os, width, width);
    }
}
