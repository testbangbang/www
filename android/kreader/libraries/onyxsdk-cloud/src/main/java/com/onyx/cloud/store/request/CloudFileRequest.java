package com.onyx.cloud.store.request;

import com.onyx.cloud.CloudManager;

/**
 * Created by suicheng on 2016/8/19.
 */
public class CloudFileRequest extends BaseCloudRequest {
    public String url;
    public String path;
    public String tag;

    public CloudFileRequest(final String path, final String url, final String tag) {
        this.path = path;
        this.url = url;
        this.tag = tag;
    }

    @Override
    public void execute(final CloudManager parent) throws Exception {
    }
}
