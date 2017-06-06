package com.onyx.edu.reader.note.model;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.scribble.formshape.FormValue;
import com.raizlabs.android.dbflow.converter.TypeConverter;

/**
 * Created by ming on 2017/6/5.
 */

public class ConverterFormValue extends TypeConverter<String, FormValue> {

    @Override
    public String getDBValue(FormValue model) {
        return JSON.toJSONString(model);
    }

    @Override
    public FormValue getModelValue(String data) {
        return JSON.parseObject(data, FormValue.class);
    }

}
