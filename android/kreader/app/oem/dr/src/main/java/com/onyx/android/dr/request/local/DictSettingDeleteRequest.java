package com.onyx.android.dr.request.local;

import com.onyx.android.dr.data.database.DictSettingEntity;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.raizlabs.android.dbflow.sql.language.Delete;

/**
 * Created by zhouzhiming on 2017/7/5.
 */
public class DictSettingDeleteRequest extends BaseDataRequest {

    public DictSettingDeleteRequest() {
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        clearData();
    }

    private void clearData() {
        new Delete().from(DictSettingEntity.class).queryList();
    }
}
