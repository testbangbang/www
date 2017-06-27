package com.onyx.android.sdk.data.model.v2;


import com.onyx.android.sdk.data.db.ContentDatabase;
import com.onyx.android.sdk.data.model.BaseData;
import com.raizlabs.android.dbflow.annotation.ColumnIgnore;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by suicheng on 2017/6/16.
 */
@Table(database = ContentDatabase.class, allFields = true)
public class DeviceBind extends BaseData {
    public String userInfoId;
    public String model;
    public String mac;
    public String installationId;
    @ColumnIgnore
    public AccountCommon info;
}
