package com.onyx.jdread.data.database;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by hehai on 17-3-7.
 */
@Database(name = JDReadStoreDatabase.NAME, version = JDReadStoreDatabase.VERSION)
public class JDReadStoreDatabase {
    public static final String NAME = "JDBookStore";
    public static final int VERSION = 1;
    public static final String AUTHORITY = "com.onyx.jdread.provider";
    public static final String BASE_CONTENT_URI = "content://";
}