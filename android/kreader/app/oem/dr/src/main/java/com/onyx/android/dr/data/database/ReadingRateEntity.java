package com.onyx.android.dr.data.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Date;

/**
 * Created by zhouzhiming on 2017/7/12.
 */
@Table(database = DRDatabase.class)
public class ReadingRateEntity extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    public long id;
    @Column
    public Date time;
    @Column
    public String bookName;
    @Column
    public String timeHorizon;
    @Column
    public String languageType;
    @Column
    public int readSummaryPiece;
    @Column
    public int readerResponsePiece;
    @Column
    public int readerResponseNumber;
    @Column
    public String md5;
    @Column
    public String language;
}