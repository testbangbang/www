package com.onyx.android.sdk.data.model.v2;

import com.onyx.android.sdk.data.db.SystemConfigDatabase;
import com.onyx.android.sdk.data.model.BaseData;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by suicheng on 2017/6/22.
 */

@Table(database = SystemConfigDatabase.class, allFields = true)
public class SystemKeyValueItem extends BaseData {
    public String key;
    public String value;

    public static SystemKeyValueItem create(String key, Object value) {
        SystemKeyValueItem item = new SystemKeyValueItem();
        item.key = key;
        item.value = String.valueOf(value);
        return item;
    }
}
