package com.onyx.android.sdk.data.converter;

import com.onyx.android.sdk.data.model.v2.PreGuidanceDetail;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.raizlabs.android.dbflow.converter.TypeConverter;

/**
 * Created by suicheng on 2017/10/23.
 */

public class PreGuidanceDetailConverter extends TypeConverter<String, PreGuidanceDetail> {

    @Override
    public String getDBValue(PreGuidanceDetail model) {
        return JSONObjectParseUtils.toJson(model);
    }

    @Override
    public PreGuidanceDetail getModelValue(String data) {
        return JSONObjectParseUtils.parseObject(data, PreGuidanceDetail.class);
    }
}
