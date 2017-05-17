package com.onyx.android.edu.db.typeconverter;


import com.onyx.android.edu.db.model.Subject;
import com.onyx.android.edu.utils.JsonUtils;
import com.raizlabs.android.dbflow.converter.TypeConverter;

/**
 * Created by ming on 16/6/27.
 */
@com.raizlabs.android.dbflow.annotation.TypeConverter
public class SubjectConverter extends TypeConverter<String,Subject> {

    @Override
    public String getDBValue(Subject model) {
        return JsonUtils.toJson(model);
    }

    @Override
    public Subject getModelValue(String data) {
        return JsonUtils.toBean(data, Subject.class);
    }
}
