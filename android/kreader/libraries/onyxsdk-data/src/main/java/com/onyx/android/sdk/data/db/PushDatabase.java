package com.onyx.android.sdk.data.db;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by suicheng on 2017/8/3.
 */
@Database(name = PushDatabase.NAME, version = PushDatabase.VERSION)
public class PushDatabase {
    public static final String NAME = "PushDatabase";
    public static final int VERSION = 1;
}
