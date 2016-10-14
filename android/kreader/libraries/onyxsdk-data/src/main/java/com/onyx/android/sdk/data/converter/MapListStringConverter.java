package com.onyx.android.sdk.data.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.raizlabs.android.dbflow.converter.TypeConverter;

import java.util.List;
import java.util.Map;

/**
 * Created by suicheng on 2016/10/8.
 */
public class MapListStringConverter extends TypeConverter<String, Map<String, List<String>>> {
    @Override
    public String getDBValue(Map<String, List<String>> model) {
        return JSON.toJSONString(model);
    }

    @Override
    public Map<String, List<String>> getModelValue(String data) {
        return JSON.parseObject(data, new TypeReference<Map<String, List<String>>>() {
        });
    }
}
