package com.onyx.android.sdk.scribble.data;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.raizlabs.android.dbflow.converter.TypeConverter;
import com.raizlabs.android.dbflow.data.Blob;
import org.nustaq.serialization.FSTConfiguration;

import java.util.List;

/**
 * Created by zhuzeng on 6/23/16.
 */
@com.raizlabs.android.dbflow.annotation.TypeConverter
public class ConverterStringList extends TypeConverter<String, PageNameList> {

    @Override
    public String getDBValue(PageNameList model) {
        return JSON.toJSONString(model);
    }

    @Override
    public PageNameList getModelValue(String data) {
        return JSON.parseObject(data, PageNameList.class);
    }

}
