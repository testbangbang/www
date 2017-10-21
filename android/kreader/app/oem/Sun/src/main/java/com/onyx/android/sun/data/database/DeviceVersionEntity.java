package com.onyx.android.sun.data.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by hehai on 17-1-22.
 */
@Table(database = SunDatabase.class)
public class DeviceVersionEntity extends BaseModel {
    public int Code;
    public String resultMessage;
    @Column
    @PrimaryKey(autoincrement = true)
    public long id;
    @Column
    public String Ver;
    @Column
    public String APKDownUrl;
}
