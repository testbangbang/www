package com.onyx.android.sdk.data.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.onyx.android.sdk.data.db.OnyxCloudDatabase;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by suicheng on 2017/3/1.
 */
@Table(database = OnyxCloudDatabase.class, allFields = true)
public class AppProduct extends Product {

    public String apkId;
    public String certMd5;

    public String type;
    @JSONField(name = "pkg")
    public String packageName;
    public int versionName;
    public int versionCode;
    public int size;
}
