package com.onyx.android.sdk.dataprovider;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by zhuzeng on 6/1/16.
 */
@Database(name = ContentDatabase.NAME, version = ContentDatabase.VERSION)
public class ContentDatabase {

    public static final String NAME = "ContentDatabase";
    public static final int VERSION = 1;

}
