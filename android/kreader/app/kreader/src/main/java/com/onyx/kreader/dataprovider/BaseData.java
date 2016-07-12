package com.onyx.kreader.dataprovider;

import android.graphics.Rect;
import com.raizlabs.android.dbflow.annotation.*;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhuzeng on 6/1/16.
 */
@Table(database = ReaderDatabase.class)
public class BaseData extends BaseModel {

    public static final int INVALID_ID = -1;
    public static final String DELIMITER = ",";

    @Column
    @PrimaryKey(autoincrement = true)
    @Index
    long id = INVALID_ID;

    @Column
    String md5 = null;

    @Column
    private Date createdAt = null;

    @Column
    private Date updatedAt = null;

    public long getId() {
        return id;
    }

    public void setId(long value) {
        id = value;
    }

    public final String getMd5() {
        return md5;
    }

    public void setMd5(final String value) {
        md5 = value;
    }

    public void setCreatedAt(final Date d) {
        createdAt = d;
    }

    public final Date getCreatedAt() {
        return createdAt;
    }

    public final Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(final Date d) {
        updatedAt = d;
    }

}
