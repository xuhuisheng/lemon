package com.mossle.disk.support;

import java.io.InputStream;
import java.io.OutputStream;

public interface PreviewHelper {
    String findPreviewType(String type);

    boolean convertPreview(String type, InputStream is, OutputStream os);
}
