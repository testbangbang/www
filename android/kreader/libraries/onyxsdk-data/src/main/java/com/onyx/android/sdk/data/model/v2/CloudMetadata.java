package com.onyx.android.sdk.data.model.v2;

import com.onyx.android.sdk.data.db.ContentDatabase;
import com.onyx.android.sdk.data.model.Metadata;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by suicheng on 2017/5/5.
 */
@Table(database = ContentDatabase.class)
public class CloudMetadata extends Metadata {

    @Override
    public String getAssociationId() {
        return getCloudId();
    }

    @Override
    public void beforeSave() {
        super.beforeSave();
        setFetchSource(FetchSource.CLOUD);
    }
}
