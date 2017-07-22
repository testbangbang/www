package com.onyx.android.dr.data.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by zhouzhiming on 2017/7/5.
 */
@Table(database = DRDatabase.class)
public class GoodSentenceNoteEntity extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    public long id;
    @Column
    public String week;
    @Column
    public String month;
    @Column
    public String day;
    @Column
    public String details;
    @Column
    public String readingMatter;
    @Column
    public String pageNumber;
    @Column
    public String recitation;
    @Column
    public long currentTime;
    @Column
    public String type;
}