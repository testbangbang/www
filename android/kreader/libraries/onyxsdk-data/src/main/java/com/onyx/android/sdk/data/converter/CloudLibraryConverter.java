package com.onyx.android.sdk.data.converter;

import com.onyx.android.sdk.data.model.v2.CloudLibrary;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.raizlabs.android.dbflow.converter.TypeConverter;

/**
 * Created by suicheng on 2017/8/4.
 */
public class CloudLibraryConverter extends TypeConverter<String, CloudLibrary> {

    @Override
    public String getDBValue(CloudLibrary model) {
        return JSONObjectParseUtils.toJson(model);
    }

    @Override
    public CloudLibrary getModelValue(String data) {
        return JSONObjectParseUtils.parseObject(data, CloudLibrary.class);
    }
}
