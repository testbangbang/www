package com.onyx.android.dr.data.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by zhouzhiming on 2017/7/6.
 */
@Table(database = DRDatabase.class)
public class NewWordNoteBookEntity extends BaseModel {
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
    public String newWord;
    @Column
    public String dictionaryLookup;
    @Column
    public String readingMatter;
    @Column
    public long currentTime;
    @Column
    public String type;
}