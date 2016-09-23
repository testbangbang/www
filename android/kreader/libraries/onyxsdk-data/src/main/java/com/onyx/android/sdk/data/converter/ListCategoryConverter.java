package com.onyx.android.sdk.data.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.onyx.android.sdk.data.model.Category;
import com.raizlabs.android.dbflow.converter.TypeConverter;

import java.util.List;

/**
 * Created by suicheng on 2016/9/23.
 */
public class ListCategoryConverter extends TypeConverter<String, List<Category>> {
    @Override
    public String getDBValue(List<Category> model) {
        return JSON.toJSONString(model);
    }

    @Override
    public List<Category> getModelValue(String data) {
        return JSON.parseObject(data, new TypeReference<List<Category>>() {
        });
    }
}
