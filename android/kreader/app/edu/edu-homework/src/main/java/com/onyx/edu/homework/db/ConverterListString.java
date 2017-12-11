package com.onyx.edu.homework.db;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.onyx.android.sdk.scribble.data.PageNameList;
import com.raizlabs.android.dbflow.converter.TypeConverter;

import java.util.List;

/**
 * Created by lxm on 2017/12/11..
 */
@com.raizlabs.android.dbflow.annotation.TypeConverter
public class ConverterListString extends TypeConverter<String, List> {

    @Override
    public String getDBValue(List model) {
        return JSON.toJSONString(model);
    }

    @Override
    public List getModelValue(String data) {
        return JSON.parseObject(data, new TypeReference<List<String>>() {});
    }

}
