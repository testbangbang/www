package com.onyx.android.sdk.data.model.common;

import android.support.annotation.Nullable;

import com.onyx.android.sdk.utils.StringUtils;

import java.io.Serializable;

/**
 * Created by suicheng on 2017/12/19.
 */
public class WaveformResult implements Serializable {
    public String url;
    public String md5;

    public static boolean checkValid(@Nullable WaveformResult result) {
        return result != null && StringUtils.isNotBlank(result.url) && StringUtils.isNotBlank(result.md5);
    }
}
