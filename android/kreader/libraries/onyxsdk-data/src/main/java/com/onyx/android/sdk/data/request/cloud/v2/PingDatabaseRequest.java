package com.onyx.android.sdk.data.request.cloud.v2;

import android.util.Log;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.db.table.EduAccountProvider;
import com.onyx.android.sdk.data.model.v2.EduAccount;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;

/**
 * Created by zhuzeng on 07/06/2017.
 */

public class PingDatabaseRequest  extends BaseCloudRequest {

    public PingDatabaseRequest() {
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        try {
            EduAccount account = ContentUtils.querySingle(EduAccountProvider.CONTENT_URI,
                    EduAccount.class, ConditionGroup.clause(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
