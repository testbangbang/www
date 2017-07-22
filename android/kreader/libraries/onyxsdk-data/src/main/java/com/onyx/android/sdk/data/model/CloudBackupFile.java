package com.onyx.android.sdk.data.model;

/**
 * Created by ming on 2017/7/22.
 */

public class CloudBackupFile {

    private String version;

    private long size;

    private String file;

    private String md5;

    private String filename;

    private String updatedAt;

    private String createdAt;

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return this.version;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getSize() {
        return this.size;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getFile() {
        return this.file;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getMd5() {
        return this.md5;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return this.filename;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedAt() {
        return this.updatedAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedAt() {
        return this.createdAt;
    }
}
