package com.onyx.android.sdk.data.model;

import com.onyx.android.sdk.data.db.OnyxCloudDatabase;
import com.onyx.android.sdk.data.utils.StoreUtils;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.Date;

/**
 * Created by suicheng on 2016/10/31.
 */
@Table(database = OnyxCloudDatabase.class, allFields = true)
public class PushBroadcast extends BaseData {
    public String title;
    public String content;
    public String action;
    public String type;
    public String url;

    public boolean isReaded;

    @Override
    public void save() {
        Date date = new Date();
        setCreatedAt(date);
        setUpdatedAt(date);
        super.save();
    }

    public static int queryUnReadedBroadcast() {
        return (int) StoreUtils.queryDataCount(PushBroadcast.class, PushBroadcast_Table.isReaded.is(false));
    }
}
