package com.onyx.android.sdk.data.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.onyx.android.sdk.data.model.Member;
import com.raizlabs.android.dbflow.converter.TypeConverter;

import java.util.List;

/**
 * Created by suicheng on 2016/9/23.
 */
public class ListMemberConverter extends TypeConverter<String, List<Member>> {
    @Override
    public String getDBValue(List<Member> model) {
        return JSON.toJSONString(model);
    }

    @Override
    public List<Member> getModelValue(String data) {
        return JSON.parseObject(data, new TypeReference<List<Member>>() {
        });
    }
}
