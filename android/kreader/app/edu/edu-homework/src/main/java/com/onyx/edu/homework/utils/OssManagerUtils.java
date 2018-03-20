package com.onyx.edu.homework.utils;

import com.onyx.android.sdk.data.manager.OssManager;
import com.onyx.android.sdk.utils.Utils;

/**
 * <pre>
 *     author : liao lin tao
 *     time   : 2018/3/19 15:03
 *     desc   :
 * </pre>
 */

public class OssManagerUtils {
    private static final String KEY_ID = "LTAIvaOi2TpicO0a";
    private static final String KEY_SECRET = "ZYFyCWNKVFvBwm7fRzqrgVCApwZGRC";
    private static final String BUCKET = "onyx-edu-img";
    private static final String ENDPOINT = "http://onyx-edu-img.onyx-international.cn";

    private static OssManager ossManager;

    public static OssManager getOssManager() {
        if (ossManager == null) {
            OssManager.OssConfig ossConfig = new OssManager.OssConfig();
            ossConfig.setBucketName(BUCKET);
            ossConfig.setEndPoint(ENDPOINT);
            ossConfig.setKeyId(KEY_ID);
            ossConfig.setKeySecret(KEY_SECRET);
            ossManager = new OssManager(Utils.getApp(), ossConfig);
        }
        return ossManager;
    }
}
