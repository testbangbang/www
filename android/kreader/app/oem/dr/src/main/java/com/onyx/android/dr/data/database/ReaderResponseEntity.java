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
public class ReaderResponseEntity extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    public long id;
    @Column
    public String bookName;
    @Column
    public Integer wordNumber;
    @Column
    public String bookId;
    @Column
    public Date updatedAt;
    @Column
    public Date createdAt;
}