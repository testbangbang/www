package com.onyx.android.sdk.data.model;

import com.onyx.android.sdk.data.model.Firmware;

import java.io.Serializable;

/**
 * Created by suicheng on 2017/3/22.
 */

public class LogCollection implements Serializable {

    public Firmware firmware;
    public String desc;
    public String zipFile;
}
