package com.onyx.android.sdk.data.db;


import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by ming on 2017/2/7.
 */

@Database(name = OnyxStatisticsDatabase.NAME, version = OnyxStatisticsDatabase.VERSION)
public class OnyxStatisticsDatabase {

    public static final String NAME = "OnyxStatisticsModel";

    public static final int VERSION = 2;

}
