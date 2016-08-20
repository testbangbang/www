package com.onyx.android.edu.db.typeconverter;


import com.onyx.android.edu.db.model.LearnSection;
import com.onyx.android.edu.utils.JsonUtils;
import com.raizlabs.android.dbflow.converter.TypeConverter;

/**
 * Created by ming on 16/6/27.
 */
@com.raizlabs.android.dbflow.annotation.TypeConverter
public class SectionConverter extends TypeConverter<String,LearnSection> {

    @Override
    public String getDBValue(LearnSection model) {
        return JsonUtils.toJson(model);
    }

    @Override
    public LearnSection getModelValue(String data) {
        return JsonUtils.toBean(data, LearnSection.class);
    }
}
