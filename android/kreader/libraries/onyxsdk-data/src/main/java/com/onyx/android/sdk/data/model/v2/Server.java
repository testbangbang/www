package com.onyx.android.sdk.data.model.v2;

import com.onyx.android.sdk.data.model.BaseData;
import com.onyx.android.sdk.utils.StringUtils;

/**
 * Created by suicheng on 2017/6/22.
 */

public class Server extends BaseData {
    public String name;
    public String ip;
    public String domain;
    public String port;
    public String address;
    public String organization;

    public String createServerHost() {
        String host = "http://";
        if (StringUtils.isNotBlank(ip)) {
            host += ip;
        } else {
            host += domain;
        }
        if (StringUtils.isNotBlank(port)) {
            host += ":" + port;
        }
        return host + "/";
    }
}