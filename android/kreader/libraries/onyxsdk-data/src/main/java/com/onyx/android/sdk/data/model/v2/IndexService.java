package com.onyx.android.sdk.data.model.v2;

import com.onyx.android.sdk.data.model.BaseData;
import com.onyx.android.sdk.utils.StringUtils;

/**
 * Created by suicheng on 2017/6/16.
 */

public class IndexService extends BaseData {

    public String model;
    public String mac;
    public String username;
    public String installationId;
    public Server server;

    public static boolean hasValidServer(IndexService service) {
        return service != null && service.server != null &&
                (StringUtils.isNotBlank(service.server.ip) || StringUtils.isNotBlank(service.server.domain));
    }
}
