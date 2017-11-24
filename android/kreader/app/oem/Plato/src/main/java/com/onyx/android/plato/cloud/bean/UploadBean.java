package com.onyx.android.plato.cloud.bean;

/**
 * Created by li on 2017/11/24.
 */

public class UploadBean {
    public int code;
    public String msg;
    public UploadResult data;

    private static class UploadResult {
        public String hash;
        public String key;
    }
}
