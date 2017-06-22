package com.onyx.android.sdk.data.db;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by suicheng on 2017/6/22.
 */
@Database(name = SystemConfigDatabase.NAME, version = SystemConfigDatabase.VERSION)
public class SystemConfigDatabase {
    public static final String NAME = "SystemConfigDatabase";
    public static final int VERSION = 1;
}
