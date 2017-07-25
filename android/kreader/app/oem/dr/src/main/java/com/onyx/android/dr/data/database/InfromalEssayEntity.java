package com.onyx.android.dr.data.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by zhouzhiming on 2017/7/5.
 */
@Table(database = DRDatabase.class)
public class InfromalEssayEntity extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    public long id;
    @Column
    public String title;
    @Column
    public String wordNumber;
    @Column
    public String content;
    @Column
    public long currentTime;
}