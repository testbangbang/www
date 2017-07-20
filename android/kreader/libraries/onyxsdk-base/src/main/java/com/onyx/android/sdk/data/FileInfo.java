package com.onyx.android.sdk.data;

/**
 * Created by ming on 2017/7/20.
 */

public class FileInfo {

    private String name;
    private Long size;
    private Long lastModified;
    private String path;
    private boolean local = true;

    public FileInfo(String name, Long lastModified, String path) {
        this.name = name;
        this.lastModified = lastModified;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getLastModified() {
        return lastModified;
    }

    public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public static FileInfo create(String name, Long lastModified, String path) {
        return new FileInfo(name, lastModified, path);
    }
}
