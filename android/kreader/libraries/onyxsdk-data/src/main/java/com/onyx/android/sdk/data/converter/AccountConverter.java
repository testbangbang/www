package com.onyx.android.sdk.data.converter;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.data.model.OnyxAccount;
import com.raizlabs.android.dbflow.converter.TypeConverter;

/**
 * Created by suicheng on 2016/9/23.
 */
public class AccountConverter extends TypeConverter<String, OnyxAccount> {
    @Override
    public String getDBValue(OnyxAccount model) {
        return JSON.toJSONString(model);
    }

    @Override
    public OnyxAccount getModelValue(String data) {
        return JSON.parseObject(data, OnyxAccount.class);
    }
}
