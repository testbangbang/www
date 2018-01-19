package com.onyx.jdread.shop.cloud.entity;

/**
 * Created by jackdeng on 2018/1/9.
 */

public class BookRankListRequestBean extends BaseRequestInfo {
    private int moduleType;
    private String type;

    public BookRankListRequestBean() {

    }

    public int getModuleType() {
        return moduleType;
    }

    public void setModuleType(int moduleType) {
        this.moduleType = moduleType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
