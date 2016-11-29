package com.onyx.android.sdk.data.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.raizlabs.android.dbflow.converter.TypeConverter;

import java.util.List;

/**
 * Created by suicheng on 2016/10/18.
 */

public class ListIntegerConverter extends TypeConverter<String, List<Integer>> {
    @Override
    public String getDBValue(List<Integer> model) {
        return JSON.toJSONString(model);
    }

    @Override
    public List<Integer> getModelValue(String data) {
        return JSON.parseObject(data, new TypeReference<List<Integer>>() {
        });
    }
}
