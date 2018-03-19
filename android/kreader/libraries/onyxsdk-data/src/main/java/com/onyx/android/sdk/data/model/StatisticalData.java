package com.onyx.android.sdk.data.model;

import com.onyx.android.sdk.data.db.ContentDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by li on 2018/3/19.
 */
@Table(database = ContentDatabase.class)
public class StatisticalData extends BaseData {
    @Column
    public String cloudId;

    @Column
    public String startReadTime;

    @Column
    public String endReadTime;

    @Column
    public String length;

    @Column
    public String current;

    @Column
    public String extraAttributes;

    @Column
    public String tag;

    @Column
    public String path;
}
