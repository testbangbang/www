package com.onyx.android.edu.db.typeconverter;


import com.onyx.android.edu.db.model.ResourceBundle;
import com.onyx.android.edu.utils.JsonUtils;
import com.raizlabs.android.dbflow.converter.TypeConverter;

/**
 * Created by ming on 16/6/27.
 */
@com.raizlabs.android.dbflow.annotation.TypeConverter
public class ResourceBundleConverter extends TypeConverter<String,ResourceBundle> {

    @Override
    public String getDBValue(ResourceBundle model) {
        return JsonUtils.toJson(model);
    }

    @Override
    public ResourceBundle getModelValue(String data) {
        return JsonUtils.toBean(data, ResourceBundle.class);
    }
}
