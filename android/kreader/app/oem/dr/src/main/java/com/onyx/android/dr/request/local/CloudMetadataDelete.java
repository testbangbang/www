package com.onyx.android.dr.request.local;

import android.util.Log;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.v2.CloudMetadata;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

/**
 * Created by zhouzhiming on 2017/7/5.
 */
public class CloudMetadataDelete extends BaseDataRequest {
    private static final String TAG = CloudMetadataDelete.class.getSimpleName();

    public CloudMetadataDelete() {
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        clearGoodSentence();
    }

    private void clearGoodSentence() {
       List<CloudMetadata> metadataBefore = new Select().from(CloudMetadata.class).queryList();
        Log.i(TAG, "Delete_before_Metadata_size:" + metadataBefore.size());
        new Delete().from(CloudMetadata.class).queryList();
        List<CloudMetadata> metadataAfter = new Select().from(CloudMetadata.class).queryList();
        Log.i(TAG, "Delete_after_Metadata_size:" + metadataAfter.size());
    }
}
