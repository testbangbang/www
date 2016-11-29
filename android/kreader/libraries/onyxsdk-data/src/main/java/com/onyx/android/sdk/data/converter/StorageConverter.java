package com.onyx.android.sdk.data.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.onyx.android.sdk.data.model.Link;
import com.raizlabs.android.dbflow.converter.TypeConverter;

import java.util.Map;

/**
 * Created by suicheng on 2016/10/18. for Product storage and cover use
 */
public class StorageConverter extends TypeConverter<String, Map<String, Map<String, Link>>> {
    @Override
    public String getDBValue(Map<String, Map<String, Link>> model) {
        return JSON.toJSONString(model);
    }

    @Override
    public Map<String, Map<String, Link>> getModelValue(String data) {
        return JSON.parseObject(data, new TypeReference<Map<String, Map<String, Link>>>() {
        });
    }
}
