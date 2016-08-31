package com.onyx.android.sdk.data.request.cloud;


import com.onyx.android.sdk.data.CloudManager;

/**
 * Created by suicheng on 2016/8/19.
 */
public class CloudFileDownloadRequest extends BaseCloudRequest {

    private String url;
    private String path;
    private String tag;
    private int state;

    public CloudFileDownloadRequest(final String url, final String path, final String tag) {
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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public void execute(final CloudManager parent) throws Exception {
    }
}
