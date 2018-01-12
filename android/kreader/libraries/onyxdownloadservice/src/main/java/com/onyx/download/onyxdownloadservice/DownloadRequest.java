package com.onyx.download.onyxdownloadservice;

/**
 * Created by 12 on 2017/1/14.
 */

public class DownloadRequest {
    private int taskId = 0;
    private String url;
    private String path;
    private String tag;
    private int state;

    public DownloadRequest(String url, String path, String tag) {
        this.url = url;
        this.path = path;
        this.tag = tag;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
