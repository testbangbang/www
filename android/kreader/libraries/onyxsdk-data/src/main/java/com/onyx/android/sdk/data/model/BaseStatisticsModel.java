package com.onyx.android.sdk.data.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Index;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Date;

/**
 * Created by ming on 2017/2/7.
 */

public abstract class BaseStatisticsModel extends BaseModel {

    public static final int DATA_TYPE_OPEN = 0;
    public static final int DATA_TYPE_PAGE_CHANGE = 1;
    public static final int DATA_TYPE_ANNOTATION = 2;
    public static final int DATA_TYPE_LOOKUP_DIC = 3;
    public static final int DATA_TYPE_TEXT_SELECTED = 4;
    public static final int DATA_TYPE_CLOSE = 5;
    public static final int DATA_TYPE_FINISH = 6;

    public static final int DATA_STATUS_NOT_PUSH = 0;
    public static final int DATA_STATUS_PUSHED = 1;

    public static final int INVALID_ID = -1;

    @Column
    @PrimaryKey(autoincrement = true)
    @Index
    long id = INVALID_ID;

    @Column
    private Integer type;
    @Column
    private String mac;
    @Column
    private String md5;
    @Column
    private String md5short;
    @Column
    private Date eventTime;
    @Column
    private String sid;
    @Column
    private Integer status = DATA_STATUS_NOT_PUSH;

    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public String getMd5short() {
        return md5short;
    }

    public void setMd5short(String md5short) {
        this.md5short = md5short;
    }
}
