package com.onyx.android.sdk.data.converter;

import com.onyx.android.sdk.data.model.v2.HomeworkDetail;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.raizlabs.android.dbflow.converter.TypeConverter;

/**
 * Created by suicheng on 2017/12/7.
 */
@com.raizlabs.android.dbflow.annotation.TypeConverter
public class HomeworkDetailConverter extends TypeConverter<String, HomeworkDetail> {

    @Override
    public String getDBValue(HomeworkDetail model) {
        return JSONObjectParseUtils.toJson(model);
    }

    @Override
    public HomeworkDetail getModelValue(String data) {
        return JSONObjectParseUtils.parseObject(data, HomeworkDetail.class);
    }
}