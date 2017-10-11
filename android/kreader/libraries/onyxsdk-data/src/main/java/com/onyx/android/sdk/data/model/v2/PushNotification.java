package com.onyx.android.sdk.data.model.v2;

import com.onyx.android.sdk.data.db.PushDatabase;
import com.onyx.android.sdk.data.model.BaseData;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;

/**
 * Created by suicheng on 2017/8/19.
 */
@Table(database = PushDatabase.class, allFields = true)
public class PushNotification extends BaseData {
    public enum Type {
        Text, Vote
    }

    public String _id;
    public String productId;
    public String title;
    public String content;
    public String type;
    public int status;
    public boolean isReaded;
}
