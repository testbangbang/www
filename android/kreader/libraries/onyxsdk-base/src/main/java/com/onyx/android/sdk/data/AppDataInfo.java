package com.onyx.android.sdk.data;

import android.content.Intent;
import android.graphics.drawable.Drawable;

/**
 * Created by suicheng on 2017/2/16.
 */
public class AppDataInfo {
    public String packageName;
    public String activityClassName;
    public String labelName;
    public long lastUpdatedTime;
    public boolean isSystemApp;
    public Drawable iconDrawable;
    public Intent intent;
}
