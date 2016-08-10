package com.onyx.android.edu.db.typeconverter;


import com.onyx.android.edu.db.model.Textbook;
import com.onyx.android.edu.utils.JsonUtils;
import com.raizlabs.android.dbflow.converter.TypeConverter;

/**
 * Created by ming on 16/6/27.
 */
@com.raizlabs.android.dbflow.annotation.TypeConverter
public class TextbookConverter extends TypeConverter<String,Textbook> {

    @Override
    public String getDBValue(Textbook model) {
        return JsonUtils.toJson(model);
    }

    @Override
    public Textbook getModelValue(String data) {
        return JsonUtils.toBean(data, Textbook.class);
    }
}
