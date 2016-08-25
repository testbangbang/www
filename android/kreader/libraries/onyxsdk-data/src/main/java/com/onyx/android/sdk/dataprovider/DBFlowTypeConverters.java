package com.onyx.android.sdk.dataprovider;

import android.graphics.RectF;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.raizlabs.android.dbflow.converter.TypeConverter;

import java.util.List;

/**
 * Created by joy on 7/8/16.
 */
public class DBFlowTypeConverters {

    @com.raizlabs.android.dbflow.annotation.TypeConverter
    public static class RectangleListConverter extends TypeConverter<String, List> {

        @Override
        public String getDBValue(List model) {
            return JSON.toJSONString(model);
        }

        @Override
        public List getModelValue(String data) {
            return JSON.parseObject(data, new TypeReference<List<RectF>>() {});
        }
    }
}
