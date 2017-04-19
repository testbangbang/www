package com.onyx.android.sdk.data.request.cloud;

import com.onyx.android.sdk.data.CloudManager;

/**
 * Created by suicheng on 2016/8/19.
 */
public class CloudFileDownloadRequest extends BaseCloudRequest {

    private int taskId = 0;
    private String url;
    private String path;
    private Object tag;
    private int state;
    private boolean md5Valid;

    public CloudFileDownloadRequest(final String url, final String path, final Object tag) {
        this.path = path;
        this.url = url;
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

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int id) {
        taskId = id;
    }

    public boolean isMd5Valid() {
        return md5Valid;
    }

    public void setMd5Valid(boolean md5Valid) {
        this.md5Valid = md5Valid;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {

    }
}
