package com.onyx.jdread.setting.data.database;

import com.onyx.android.sdk.data.model.BaseData;
import com.onyx.android.sdk.data.model.Firmware;
import com.onyx.android.sdk.data.model.LogCollection;
import com.onyx.jdread.main.common.JSONConverter;
import com.onyx.jdread.main.data.database.JDReadDatabase;
import com.raizlabs.android.dbflow.annotation.Index;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by suicheng on 2018/2/1.
 */
@Table(database = JDReadDatabase.class, allFields = true)
public class FeedbackRecord extends LogCollection implements Serializable {

    @PrimaryKey(autoincrement = true)
    public long id = BaseData.INVALID_ID;

    public Date createdAt;
    public Date updatedAt;

    public void updateDate() {
        if (createdAt == null) {
            createdAt = new Date();
        }
        updatedAt = new Date();
    }

    @com.raizlabs.android.dbflow.annotation.TypeConverter
    public static class Converter extends JSONConverter<String, Firmware> {
    }
}
