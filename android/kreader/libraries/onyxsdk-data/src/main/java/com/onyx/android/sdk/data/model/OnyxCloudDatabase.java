package com.onyx.android.sdk.data.model;

import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = OnyxCloudDatabase.NAME, version = OnyxCloudDatabase.VERSION)
public class OnyxCloudDatabase {
	
	public static final String NAME = "OnyxCloud";
	
	public static final int VERSION = 1;

}
