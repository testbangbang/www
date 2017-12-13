package com.onyx.edu.homework.db;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.data.model.QuestionReview;
import com.raizlabs.android.dbflow.converter.TypeConverter;

/**
 * Created by lxm on 2017/12/11..
 */
@com.raizlabs.android.dbflow.annotation.TypeConverter
public class ConverterQuestionReview extends TypeConverter<String, QuestionReview> {

    @Override
    public String getDBValue(QuestionReview model) {
        return JSON.toJSONString(model);
    }

    @Override
    public QuestionReview getModelValue(String data) {
        return JSON.parseObject(data, QuestionReview.class);
    }

}
