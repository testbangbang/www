package com.onyx.android.sdk.data.request.cloud.v2;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.model.v2.PushNotification;
import com.onyx.android.sdk.data.model.v2.PushNotification_Table;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.Where;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by suicheng on 2017/8/19.
 */
public class PushNotificationLoadRequest extends BaseCloudRequest {

    private List<String> idList;
    private List<PushNotification> notificationList = new ArrayList<>();

    private boolean useMap;
    private Map<String, PushNotification> idMap = new HashMap<>();

    public PushNotificationLoadRequest(boolean useMap) {
        this.useMap = useMap;
    }

    public PushNotificationLoadRequest(List<String> idList, boolean useMap) {
        this.idList = idList;
        this.useMap = useMap;
    }

    public List<PushNotification> getNotificationList() {
        return notificationList;
    }

    public Map<String, PushNotification> getNotificationMap() {
        return idMap;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Where<PushNotification> where = new Select().from(PushNotification.class).where();
        ConditionGroup condition = getIdsCondition();
        if (condition != null) {
            where.and(condition);
        }
        notificationList = where.queryList();
        if (useMap && !CollectionUtils.isNullOrEmpty(notificationList)) {
            for (PushNotification item : notificationList) {
                idMap.put(item.productId, item);
            }
        }
    }

    private ConditionGroup getIdsCondition() {
        return DataManagerHelper.getPropertyOrCondition(idList, PushNotification_Table.productId);
    }
}
