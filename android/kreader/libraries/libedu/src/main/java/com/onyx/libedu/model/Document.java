package com.onyx.libedu.model;

import com.onyx.android.sdk.data.model.BaseData;

/**
 * Created by ming on 2016/11/30.
 */

public class Document extends BaseData {

    private String name;
    private String softType;
    private String softSize;
    private String author;
    private int areaId;
    private String copyrightType;

    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCopyrightType() {
        return copyrightType;
    }

    public void setCopyrightType(String copyrightType) {
        this.copyrightType = copyrightType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSoftSize() {
        return softSize;
    }

    public void setSoftSize(String softSize) {
        this.softSize = softSize;
    }

    public String getSoftType() {
        return softType;
    }

    public void setSoftType(String softType) {
        this.softType = softType;
    }
}
