package com.onyx.android.sdk.data.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.raizlabs.android.dbflow.converter.TypeConverter;

import java.util.Set;

/**
 * Created by suicheng on 2016/10/18.
 */

public class SetStringConverter extends TypeConverter<String, Set<String>> {
    @Override
    public String getDBValue(Set<String> model) {
        return JSON.toJSONString(model);
    }

    @Override
    public Set<String> getModelValue(String data) {
        return JSON.parseObject(data, new TypeReference<Set<String>>() {
        });
    }
}
