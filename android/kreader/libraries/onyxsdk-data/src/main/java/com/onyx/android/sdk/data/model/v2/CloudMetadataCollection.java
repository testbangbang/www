package com.onyx.android.sdk.data.model.v2;

import com.onyx.android.sdk.data.db.ContentDatabase;
import com.onyx.android.sdk.data.model.MetadataCollection;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by suicheng on 2017/5/17.
 */
@Table(database = ContentDatabase.class)
public class CloudMetadataCollection extends MetadataCollection {
}
