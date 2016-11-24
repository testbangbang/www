package com.onyx.android.sdk.data.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.onyx.android.sdk.data.db.OnyxCloudDatabase;
import com.raizlabs.android.dbflow.annotation.ColumnIgnore;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by suicheng on 2016/10/14.
 */

@Table(database = OnyxCloudDatabase.class)
public class PushProduct extends Product {

    @JSONField(serialize = false, deserialize = false)
    @ColumnIgnore
    public boolean isFiLeExist = false;

    public PushProduct() {
    }
}
