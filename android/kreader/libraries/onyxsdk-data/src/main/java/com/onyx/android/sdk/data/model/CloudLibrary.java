package com.onyx.android.sdk.data.model;

import com.onyx.android.sdk.data.db.ContentDatabase;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by suicheng on 2017/5/16.
 */
@Table(database = ContentDatabase.class)
public class CloudLibrary extends Library {
}
