package com.onyx.android.sdk.data.converter;

import com.alibaba.fastjson.TypeReference;
import com.onyx.android.sdk.data.model.v2.CloudGroup;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.raizlabs.android.dbflow.converter.TypeConverter;

import java.util.List;

/**
 * Created by suicheng on 2017/7/22.
 */
public class ListCloudGroupConverter extends TypeConverter<String, List<CloudGroup>> {

    @Override
    public String getDBValue(List<CloudGroup> model) {
        return JSONObjectParseUtils.toJson(model);
    }

    @Override
    public List<CloudGroup> getModelValue(String data) {
        return JSONObjectParseUtils.parseObject(data, new TypeReference<List<CloudGroup>>() {
        });
    }
}
