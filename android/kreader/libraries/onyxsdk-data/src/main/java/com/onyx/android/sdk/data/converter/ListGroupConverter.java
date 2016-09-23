package com.onyx.android.sdk.data.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.onyx.android.sdk.data.model.OnyxGroup;
import com.raizlabs.android.dbflow.converter.TypeConverter;

import java.util.List;

/**
 * Created by suicheng on 2016/9/23.
 */
public class ListGroupConverter extends TypeConverter<String, List<OnyxGroup>> {
    @Override
    public String getDBValue(List<OnyxGroup> model) {
        return JSON.toJSONString(model);
    }

    @Override
    public List<OnyxGroup> getModelValue(String data) {
        return JSON.parseObject(data, new TypeReference<List<OnyxGroup>>() {
        });
    }
}
