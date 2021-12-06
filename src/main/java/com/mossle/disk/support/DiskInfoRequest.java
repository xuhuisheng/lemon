package com.mossle.disk.support;

public class DiskInfoRequest {
    private String userId;
    private long folderCode;
    private String name;
    private long size;
    private String type;
    private boolean fileType;
    private String ref;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getFolderCode() {
        return folderCode;
    }

    public void setFolderCode(long folderCode) {
        this.folderCode = folderCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isFileType() {
        return fileType;
    }

    public void setFileType(boolean fileType) {
        this.fileType = fileType;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }
}
