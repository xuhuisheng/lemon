package com.mossle.ext.store;

import java.io.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.UUID;

import org.springframework.util.FileCopyUtils;

public class FileStoreConnector implements StoreConnector {
    private String baseDir;

    public StoreDTO save(String model, InputStream inputStream,
            String originName) throws Exception {
        String prefix = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String suffix = this.getSuffix(originName);
        String path = prefix + "/" + UUID.randomUUID() + suffix;
        File dir = new File(baseDir + "/" + model + "/" + prefix);
        dir.mkdirs();

        File targetFile = new File(baseDir + "/" + model + "/" + path);
        FileOutputStream fos = new FileOutputStream(targetFile);

        try {
            FileCopyUtils.copy(inputStream, fos);
            fos.flush();
        } finally {
            fos.close();
        }

        StoreDTO storeDto = new StoreDTO();
        storeDto.setModel(model);
        storeDto.setKey(path);
        storeDto.setInputStream(inputStream);

        return storeDto;
    }

    public StoreDTO get(String model, String key) throws Exception {
        if (key.indexOf("../") != -1) {
            StoreDTO storeDto = new StoreDTO();
            storeDto.setModel(model);
            storeDto.setKey(key);
            storeDto.setInputStream(new ByteArrayInputStream(new byte[0]));
        }

        File file = new File(baseDir + "/" + model + "/" + key);
        StoreDTO storeDto = new StoreDTO();
        storeDto.setModel(model);
        storeDto.setKey(key);
        storeDto.setInputStream(new FileInputStream(file));

        return storeDto;
    }

    public String getSuffix(String name) {
        int lastIndex = name.lastIndexOf(".");

        if (lastIndex != -1) {
            return name.substring(lastIndex);
        } else {
            return "";
        }
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }
}
