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

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            return true;
        }
        if (obj == null || !(obj instanceof IndexService)) {
            return false;
        }
        Server targetServer = ((IndexService) obj).server;
        if (targetServer == null) {
            return false;
        }
        return server.getApiBase().equals(targetServer.getApiBase());
    }
}
