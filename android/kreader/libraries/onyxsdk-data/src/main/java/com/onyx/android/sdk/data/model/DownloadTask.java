package com.onyx.android.sdk.data.model;

import com.raizlabs.android.dbflow.annotation.Table;
import java.util.Date;

/**
 * Created by suicheng on 2016/8/22.
 */
@Table(database = OnyxCloudDatabase.class, allFields = true)
public class DownloadTask extends BaseData {

    public int taskId;
    public String name;
    public String url;
    public String path;
    public Date finishedAt;

    public DownloadTask() {
        setCreatedAt(new Date());
    }
}
