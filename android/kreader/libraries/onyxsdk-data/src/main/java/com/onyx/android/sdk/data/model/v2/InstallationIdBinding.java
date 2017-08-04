package com.onyx.android.sdk.data.model.v2;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by suicheng on 2017/8/2.
 */
public class InstallationIdBinding implements Serializable {
    public String mac;
    public Map<String, String> installationMap;
}
