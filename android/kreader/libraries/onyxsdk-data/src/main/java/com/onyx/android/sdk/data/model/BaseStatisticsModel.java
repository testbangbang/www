package com.onyx.android.sdk.data.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Index;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Date;

/**
 * Created by ming on 2017/2/7.
 */

public abstract class BaseStatisticsModel extends BaseModel implements Cloneable{

    public static final int DATA_TYPE_OPEN = 0;
    public static final int DATA_TYPE_PAGE_CHANGE = 1;
    public static final int DATA_TYPE_ANNOTATION = 2;
    public static final int DATA_TYPE_LOOKUP_DIC = 3;
    public static final int DATA_TYPE_TEXT_SELECTED = 4;
    public static final int DATA_TYPE_CLOSE = 5;
    public static final int DATA_TYPE_FINISH = 6;

    public static final int DATA_STATUS_TEST = -1;
    public static final int DATA_STATUS_NOT_PUSH = 0;
    public static final int DATA_STATUS_PUSHED = 1;
    public static final int DATA_STATUS_FROM_OREADER = 2;


    public static final int INVALID_ID = -1;

    @Column
    @PrimaryKey(autoincrement = true)
    @Index
    long id = INVALID_ID;

    @Column
    protected Integer type;
    @Column
    protected String mac;
    @Column
    protected String md5;
    @Column
    protected String md5short;
    @Column
    protected Date eventTime;
    @Column
    protected String sid;
    @Column
    protected Integer status = DATA_STATUS_NOT_PUSH;

    public BaseStatisticsModel() {
    }

    public BaseStatisticsModel(Date eventTime, long id, String mac, String md5, String md5short, String sid, Integer status, Integer type) {
        this.eventTime = eventTime;
        this.id = id;
        this.mac = mac;
        this.md5 = md5;
        this.md5short = md5short;
        this.sid = sid;
        this.status = status;
        this.type = type;
    }

    public BaseStatisticsModel(String md5, String md5short, String sid, Integer type, Date eventTime) {
        this.md5 = md5;
        this.md5short = md5short;
        this.sid = sid;
        this.type = type;
        this.eventTime = eventTime;
    }

    public BaseStatisticsModel(String md5, String md5short, String sid, Integer type, Date eventTime, int status) {
        this.md5 = md5;
        this.md5short = md5short;
        this.sid = sid;
        this.type = type;
        this.eventTime = eventTime;
        this.status = status;
    }

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
