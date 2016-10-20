package com.onyx.kreader.utils;

import android.content.Context;
import android.os.Build;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.utils.RawResourceUtil;
import com.onyx.android.sdk.utils.StringUtils;

/**
 * Created by ming on 16/10/18.
 */
public class ReaderConfig {

    private static ReaderConfig ourInstance;

    private ReaderConfig(Context context) {
        String content = contentFromRawResource(context, Build.MODEL.toString());
        if (!StringUtils.isNullOrEmpty(content)) {
            ourInstance = JSON.parseObject(content, ReaderConfig.class);
        }
        if (ourInstance == null) {
            ourInstance = new ReaderConfig();
        }
    }

    private String contentFromRawResource(Context context, String name) {
        String content = "";
        try {
            int res = context.getResources().getIdentifier(name.toLowerCase(), "raw", context.getPackageName());
            content = RawResourceUtil.contentOfRawResource(context, res);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return content;
        }
    }

    public ReaderConfig() {
    }

    static public ReaderConfig sharedInstance(Context context) {
        if (ourInstance == null) {
            new ReaderConfig(context);
        }
        return ourInstance;
    }

    private boolean disable_writing = false;

    public void setDisable_writing(boolean disable_writing) {
        this.disable_writing = disable_writing;
    }

    public boolean isDisable_writing() {
        return disable_writing;
    }
}
