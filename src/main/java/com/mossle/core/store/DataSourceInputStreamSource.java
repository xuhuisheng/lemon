package com.mossle.core.store;

import java.io.IOException;
import java.io.InputStream;

import javax.activation.DataSource;

import org.springframework.core.io.InputStreamSource;

public class DataSourceInputStreamSource implements InputStreamSource {
    private DataSource dataSource;

    public DataSourceInputStreamSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public InputStream getInputStream() throws IOException {
        return dataSource.getInputStream();
    }
}
