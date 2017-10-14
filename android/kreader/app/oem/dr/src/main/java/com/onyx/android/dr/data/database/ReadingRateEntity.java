package com.onyx.android.dr.data.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by zhouzhiming on 2017/7/12.
 */
@Table(database = DRDatabase.class)
public class ReadingRateEntity extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    public long id;
    @Column
    public String recordDate;
    @Column
    public String cloudId;
    @Column
    public String name;
    @Column
    public String book;
    @Column
    public int readTimeLong;
    @Column
    public int wordsCount;
    @Column
    public String language;
    @Column
    public int speed;
    @Column
    public int summaryCount;
    @Column
    public int impressionCount;
    @Column
    public int impressionWordsCount;
}