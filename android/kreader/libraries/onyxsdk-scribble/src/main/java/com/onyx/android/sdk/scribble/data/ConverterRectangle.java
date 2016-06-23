package com.onyx.android.sdk.scribble.data;

import android.graphics.RectF;
import com.alibaba.fastjson.JSON;
import com.raizlabs.android.dbflow.converter.TypeConverter;

/**
 * Created by zhuzeng on 6/4/16.
 */
@com.raizlabs.android.dbflow.annotation.TypeConverter
public class ConverterRectangle extends TypeConverter<String, RectF> {

    @Override
    public RectF getModelValue(String string) {
        return JSON.parseObject(string, RectF.class);
    }

    @Override
    public String getDBValue(final RectF rect) {
        return JSON.toJSONString(rect);
    }

}
