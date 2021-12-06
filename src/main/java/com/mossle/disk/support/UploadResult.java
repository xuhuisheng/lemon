package com.mossle.disk.support;

import com.mossle.disk.persistence.domain.DiskInfo;
import com.mossle.disk.persistence.domain.DiskVersion;

public class UploadResult {
    private int code = 0;
    private String message = "ok";
    private DiskInfo file;
    private DiskVersion version;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DiskInfo getFile() {
        return file;
    }

    public void setFile(DiskInfo file) {
        this.file = file;
    }

    public DiskVersion getVersion() {
        return version;
    }

    public void setVersion(DiskVersion version) {
        this.version = version;
    }

    public static UploadResult validateFail(String message) {
        UploadResult uploadResult = new UploadResult();
        uploadResult.setCode(400);
        uploadResult.setMessage(message);

        return uploadResult;
    }
}
