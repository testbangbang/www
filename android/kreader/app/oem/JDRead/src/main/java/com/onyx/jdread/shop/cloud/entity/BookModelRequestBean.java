package com.onyx.jdread.shop.cloud.entity;

/**
 * Created by jackdeng on 2018/1/9.
 */

public class BookModelRequestBean extends BaseRequestInfo {
    private int fType;
    private int moduleId;

    public BookModelRequestBean() {

    }

    public int getfType() {
        return fType;
    }

    public void setfType(int fType) {
        this.fType = fType;
    }

    public int getModuleId() {
        return moduleId;
    }

    public void setModuleId(int moduleId) {
        this.moduleId = moduleId;
    }
}
