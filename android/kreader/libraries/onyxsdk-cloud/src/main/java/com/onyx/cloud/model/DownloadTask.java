package com.onyx.cloud.model;

import com.onyx.cloud.OnyxCloudDatabase;
import com.raizlabs.android.dbflow.annotation.Table;
import java.util.Date;

/**
 * Created by suicheng on 2016/8/22.
 */
@Table(database = OnyxCloudDatabase.class, allFields = true)
public class DownloadTask extends BaseObject {

    public int taskId;
    public String name;
    public String url;
    public String path;
    public Date finishedAt;

    public DownloadTask() {
        createdAt = new Date();
    }
}
