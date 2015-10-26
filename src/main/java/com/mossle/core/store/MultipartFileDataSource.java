package com.mossle.core.store;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

import org.springframework.web.multipart.MultipartFile;

public class MultipartFileDataSource implements DataSource {
    private MultipartFile multipartFile;

    public MultipartFileDataSource(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
    }

    public String getName() {
        return multipartFile.getOriginalFilename();
    }

    public InputStream getInputStream() throws IOException {
        return multipartFile.getInputStream();
    }

    public OutputStream getOutputStream() {
        throw new IllegalStateException("not support");
    }

    public String getContentType() {
        return multipartFile.getContentType();
    }
}
