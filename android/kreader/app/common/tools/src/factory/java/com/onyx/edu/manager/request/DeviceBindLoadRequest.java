package com.onyx.edu.manager.request;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.v2.DeviceBind;
import com.onyx.android.sdk.data.model.v2.DeviceBind_Table;
import com.onyx.android.sdk.data.request.data.db.BaseDBRequest;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

/**
 * Created by suicheng on 2017/6/27.
 */

public class DeviceBindLoadRequest extends BaseDBRequest {

    private boolean onlyCount = false;
    private QueryResult<DeviceBind> queryResult;

    public DeviceBindLoadRequest(boolean onlyCount) {
        this.onlyCount = onlyCount;
    }

    public QueryResult<DeviceBind> getQueryResult() {
        return queryResult;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        queryResult = new QueryResult<>();
        queryResult.fetchSource = Metadata.FetchSource.LOCAL;
        queryResult.count = count();
        if (!onlyCount) {
            queryResult.list = loadDeviceBindList();
        }
    }

    private List<DeviceBind> loadDeviceBindList() {
        return new Select().from(DeviceBind.class).where().queryList();
    }

    private long count() {
        return new Select(Method.count(DeviceBind_Table.mac.distinct()))
                .from(DeviceBind.class).where().count();
    }
}
