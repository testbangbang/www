package com.onyx.android.dr.reader.data;

import com.onyx.android.dr.data.database.DRDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by hehai on 17-8-24.
 */
@Table(database = DRDatabase.class)
public class ReadSummaryEntity extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    public long id;
    @Column
    public String bookName;
    @Column
    public String pageNumber;
    @Column
    public String summary;
    @Column
    public String newWordList;
    @Column
    public String goodSentenceList;
}
