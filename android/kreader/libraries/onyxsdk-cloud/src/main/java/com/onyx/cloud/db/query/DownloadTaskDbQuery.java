package com.onyx.cloud.db.query;

import com.onyx.cloud.model.DownloadTask;
import com.onyx.cloud.model.DownloadTask_Table;

/**
 * Created by suicheng on 2016/8/22.
 */
public class DownloadTaskDbQuery extends DbQueryBase<DownloadTask>{

    public DownloadTaskDbQuery() {
        super(DownloadTask.class);
    }

    public DownloadTaskDbQuery andNullFinishedAt() {
        where.and(DownloadTask_Table.finishedAt.isNull());
        return this;
    }


}
