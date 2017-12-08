package com.onyx.android.sdk.data.model.v2;

import com.onyx.android.sdk.data.converter.HomeworkDetailConverter;
import com.onyx.android.sdk.data.converter.ListStringConverter;
import com.onyx.android.sdk.data.db.ContentDatabase;
import com.onyx.android.sdk.data.model.BaseData;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by suicheng on 2017/11/1.
 */
@Table(database = ContentDatabase.class, allFields = true)
public class Homework extends BaseData {
    public String _id;
    public String parent;
    public String ref;
    public String title;
    public String info;
    @Column(typeConverter = ListStringConverter.class)
    public List<String> category = new ArrayList<>();
    public String subject;

    @Column(typeConverter = ListStringConverter.class)
    public List<String> resources;
    @Column(typeConverter = HomeworkDetailConverter.class)
    public HomeworkDetail child;

    public int status;
    public int ordinal;

    public int difficulty;
    public int QuesType;
    public boolean isCollect;
    public boolean published;
    public boolean needReply;

    public Date beginTime;
    public Date endTime;
}
