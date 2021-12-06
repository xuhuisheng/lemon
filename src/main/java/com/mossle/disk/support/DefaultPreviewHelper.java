package com.mossle.disk.support;

import java.io.InputStream;
import java.io.OutputStream;

public class DefaultPreviewHelper implements PreviewHelper {
    public String findPreviewType(String type) {
        if ("jpg".equals(type) || "jpeg".equals(type) || "png".equals(type)
                || "gif".equals(type) || "bmp".equals(type)
                || "ico".equals(type)) {
            return "image";
        }

        if ("docx".equals(type) || "doc".equals(type) || "xlsx".equals(type)
                || "xls".equals(type) || "pptx".equals(type)
                || "ppt".equals(type)) {
            return "pdf";
        }

        if ("pdf".equals(type)) {
            return "pdf";
        }

        if ("mp3".equals(type)) {
            return "audio";
        }

        if ("mp4".equals(type)) {
            return "video";
        }

        if ("txt".equals(type) || "html".equals(type) || "xml".equals(type)
                || "java".equals(type) || "properties".equals(type)
                || "sql".equals(type) || "js".equals(type) || "md".equals(type)
                || "json".equals(type) || "conf".equals(type)
                || "ini".equals(type) || "vue".equals(type)
                || "php".equals(type) || "py".equals(type)
                || "bat".equals(type) || "gitignore".equals(type)
                || "log".equals(type) || "htm".equals(type)
                || "css".equals(type) || "cnf".equals(type)) {
            return "txt";
        }

        /*
         * if ("zip".equals(type)) { return "zip"; }
         */

        // if ("dwg".equals(type)) {
        // return "dwg";
        // }
        return null;
    }

    public boolean convertPreview(String type, InputStream is, OutputStream os) {
        return false;
    }
}
