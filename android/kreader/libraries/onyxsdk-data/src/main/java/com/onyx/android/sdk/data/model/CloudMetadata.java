package com.onyx.android.sdk.data.model;

import com.onyx.android.sdk.data.db.ContentDatabase;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by suicheng on 2017/5/5.
 */
@Table(database = ContentDatabase.class)
public class CloudMetadata extends Metadata {

    @Override
    public void beforeSave() {
        super.beforeSave();
        setFetchSource(FetchSource.CLOUD);
    }
}
