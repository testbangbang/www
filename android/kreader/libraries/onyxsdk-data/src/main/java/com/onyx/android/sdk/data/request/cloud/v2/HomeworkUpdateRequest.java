package com.onyx.android.sdk.data.request.cloud.v2;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.Homework;
import com.onyx.android.sdk.data.model.v2.Homework_Table;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.raizlabs.android.dbflow.sql.language.SQLOperator;
import com.raizlabs.android.dbflow.sql.language.SQLite;

/**
 * Created by suicheng on 2018/1/19.
 */
public class HomeworkUpdateRequest extends BaseCloudRequest {

    private String id;
    private SQLOperator[] updateSet;

    public HomeworkUpdateRequest(String id, SQLOperator[] updateConditions) {
        this.id = id;
        this.updateSet = updateConditions;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        SQLite.update(Homework.class)
                .set(updateSet)
                .where(Homework_Table._id.eq(id))
                .execute();
    }
}
