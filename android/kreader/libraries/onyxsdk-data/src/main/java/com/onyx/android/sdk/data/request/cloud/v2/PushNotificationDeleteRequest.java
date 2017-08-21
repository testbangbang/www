package com.onyx.android.sdk.data.request.cloud.v2;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.model.v2.PushNotification;
import com.onyx.android.sdk.data.model.v2.PushNotification_Table;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.Delete;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2017/8/20.
 */
public class PushNotificationDeleteRequest extends BaseCloudRequest {

    private List<String> idList = new ArrayList<>();

    public PushNotificationDeleteRequest(List<String> idList) {
        this.idList = idList;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        if (CollectionUtils.isNullOrEmpty(idList)) {
            return;
        }
        new Delete().from(PushNotification.class)
                .where()
                .and(getIdsCondition()).execute();
    }

    private ConditionGroup getIdsCondition() {
        return DataManagerHelper.getPropertyOrCondition(idList, PushNotification_Table.productId);
    }
}
