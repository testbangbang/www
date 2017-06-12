package com.onyx.android.edu.db.typeconverter;


import com.onyx.android.edu.utils.JsonUtils;
import com.raizlabs.android.dbflow.converter.TypeConverter;

import java.util.Map;

/**
 * Created by ming on 16/6/27.
 */
public class QuizIdMapConverter extends TypeConverter<String,Map> {

    @Override
    public String getDBValue(Map model) {
        return JsonUtils.toJson(model);
    }

    @Override
    public Map<Long,Integer> getModelValue(String data) {
        return JsonUtils.toBean(data, Map.class);
    }
}
