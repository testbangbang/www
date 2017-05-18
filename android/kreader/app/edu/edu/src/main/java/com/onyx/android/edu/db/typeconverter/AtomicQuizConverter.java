package com.onyx.android.edu.db.typeconverter;


import com.onyx.android.edu.db.model.AtomicQuiz;
import com.onyx.android.edu.utils.JsonUtils;
import com.raizlabs.android.dbflow.converter.TypeConverter;

/**
 * Created by ming on 16/6/27.
 */
public class AtomicQuizConverter extends TypeConverter<String,AtomicQuiz> {

    @Override
    public String getDBValue(AtomicQuiz model) {
        return JsonUtils.toJson(model);
    }

    @Override
    public AtomicQuiz getModelValue(String data) {
        return JsonUtils.toBean(data, AtomicQuiz.class);
    }
}
