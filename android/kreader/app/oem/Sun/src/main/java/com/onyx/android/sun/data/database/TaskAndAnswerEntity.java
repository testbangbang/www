package com.onyx.android.sun.data.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by li on 2017/10/18.
 */
@Table(database = SunDatabase.class)
public class TaskAndAnswerEntity extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    public long id;
    @Column
    public String taskId;
    @Column
    public String type;
    @Column
    public String question;
    @Column
    public String userAnswer;
}
