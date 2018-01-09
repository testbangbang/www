package com.onyx.edu.homework.db;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.onyx.android.sdk.data.model.Subject;
import com.onyx.android.sdk.data.model.homework.QuestionOption;
import com.raizlabs.android.dbflow.converter.TypeConverter;

import java.util.List;

/**
 * Created by lxm on 2018/1/9.
 */
@com.raizlabs.android.dbflow.annotation.TypeConverter
public class ConverterSubject extends TypeConverter<String, Subject> {

    @Override
    public String getDBValue(Subject model) {
        return JSON.toJSONString(model);
    }

    @Override
    public Subject getModelValue(String data) {
        return JSON.parseObject(data, Subject.class);
    }

}
