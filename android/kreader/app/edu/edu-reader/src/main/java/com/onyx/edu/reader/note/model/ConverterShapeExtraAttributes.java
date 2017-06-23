package com.onyx.edu.reader.note.model;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.scribble.data.ShapeExtraAttributes;
import com.raizlabs.android.dbflow.converter.TypeConverter;

/**
 * Created by ming on 2017/6/23.
 */

public class ConverterShapeExtraAttributes extends TypeConverter<String, ShapeExtraAttributes> {

    @Override
    public String getDBValue(ShapeExtraAttributes model) {
        return JSON.toJSONString(model);
    }

    @Override
    public ShapeExtraAttributes getModelValue(String data) {
        return JSON.parseObject(data, ShapeExtraAttributes.class);
    }

}
