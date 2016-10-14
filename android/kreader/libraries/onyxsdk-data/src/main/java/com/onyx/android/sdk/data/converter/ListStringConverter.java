package com.onyx.android.sdk.data.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.raizlabs.android.dbflow.converter.TypeConverter;

import java.util.List;

/**
 * Created by suicheng on 2016/9/26.
 */
public class ListStringConverter extends TypeConverter<String, List<String>> {
    @Override
    public String getDBValue(List<String> model) {
        return JSON.toJSONString(model);
    }

    @Override
    public List<String> getModelValue(String data) {
        return JSON.parseObject(data, new TypeReference<List<String>>() {
        });
    }
}
