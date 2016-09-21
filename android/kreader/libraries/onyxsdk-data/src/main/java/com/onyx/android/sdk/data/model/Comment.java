package com.onyx.android.sdk.data.model;

import com.onyx.android.sdk.data.db.OnyxCloudDatabase;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by suicheng on 2016/9/19.
 */
@Table(database = OnyxCloudDatabase.class, allFields = true)
public class Comment extends BaseData {
    public String productId;
    public String content;
    public String title;
    public String commentatorName;
    public String commentatorAvatarUrl;
    public String commentatorEmail;
    public int commentatorId;
    public int downCount;
    public int upCount;
    public int rating;
}
