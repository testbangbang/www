package com.onyx.android.sdk.data.model.v2;

import com.onyx.android.sdk.data.db.PushDatabase;
import com.onyx.android.sdk.data.model.BaseData;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by suicheng on 2017/8/2.
 */
@Table(database = PushDatabase.class, allFields = true)
public class PushTextEvent extends BaseData {
    public String title;
    public String content;
    public boolean hasReaded;
}
