package com.onyx.android.edu.db.typeconverter;


import com.onyx.android.edu.utils.JsonUtils;
import com.raizlabs.android.dbflow.converter.TypeConverter;

import java.util.List;

/**
 * Created by ming on 16/6/27.
 */
public class StringListConverter extends TypeConverter<String,List> {

    @Override
    public String getDBValue(List model) {
        return JsonUtils.toJson(model);
    }

    @Override
    public List<String> getModelValue(String data) {
        return JsonUtils.toBean(data, List.class);
    }
}
