package com.onyx.android.sdk.data.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.onyx.android.sdk.data.model.People;
import com.raizlabs.android.dbflow.converter.TypeConverter;

import java.util.Set;

/**
 * Created by suicheng on 2016/10/18.
 */
public class SetPeopleConverter extends TypeConverter<String, Set<People>> {
    @Override
    public String getDBValue(Set<People> model) {
        return JSON.toJSONString(model);
    }

    @Override
    public Set<People> getModelValue(String data) {
        return JSON.parseObject(data, new TypeReference<Set<People>>() {
        });
    }
}
