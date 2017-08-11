package com.onyx.android.sdk.data.converter;

import com.onyx.android.sdk.data.model.v2.CloudMetadata;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.raizlabs.android.dbflow.converter.TypeConverter;

/**
 * Created by suicheng on 2017/8/4.
 */
public class CloudMetadataConverter extends TypeConverter<String, CloudMetadata> {

    @Override
    public String getDBValue(CloudMetadata model) {
        return JSONObjectParseUtils.toJson(model);
    }

    @Override
    public CloudMetadata getModelValue(String data) {
        return JSONObjectParseUtils.parseObject(data, CloudMetadata.class);
    }
}
