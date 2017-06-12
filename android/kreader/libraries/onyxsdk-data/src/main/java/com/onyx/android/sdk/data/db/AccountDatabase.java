package com.onyx.android.sdk.data.db;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by zhuzeng on 9/2/16.
 */
@Database(name = AccountDatabase.NAME, version = AccountDatabase.VERSION)
public class AccountDatabase {

    public static final String NAME = "AccountDatabase";

    public static final int VERSION = 1;
}
