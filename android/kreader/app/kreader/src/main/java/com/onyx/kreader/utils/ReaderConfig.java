package com.onyx.kreader.utils;

import android.content.Context;
import android.os.Build;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.onyx.android.sdk.utils.RawResourceUtil;
import com.onyx.kreader.R;

import java.util.Map;

/**
 * Created by ming on 16/10/18.
 */
public class ReaderConfig {

    private static ReaderConfig ourInstance;

    private ReaderConfig(Context context) {
        String content = RawResourceUtil.contentOfRawResource(context, R.raw.reader_config);
        Map<String, ReaderConfig> deviceConfigMap = JSON.parseObject(content, new TypeReference<Map<String, ReaderConfig>>() {});
        String currentDevice = Build.MODEL.toString();
        ourInstance = deviceConfigMap.get(currentDevice);
        if (ourInstance == null) {
            ourInstance = new ReaderConfig();
        }
    }

    public ReaderConfig() {}

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
