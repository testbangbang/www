package com.onyx.android.dr.bean;

/**
 * Created by hehai on 17-8-31.
 */

public class CustomAppInfo {
    private Class activityClass;
    private int labelName;
    private int resource;

    public CustomAppInfo(Class activityClass, int labelName, int resource) {
        this.activityClass = activityClass;
        this.labelName = labelName;
        this.resource = resource;
    }

    public Class getActivityClass() {
        return activityClass;
    }

    public int getLabelName() {
        return labelName;
    }

    public int getResource() {
        return resource;
    }
}
