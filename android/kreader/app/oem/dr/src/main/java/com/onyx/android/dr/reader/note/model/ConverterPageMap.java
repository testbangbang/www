package com.onyx.android.dr.reader.note.model;

import com.alibaba.fastjson.JSON;
import com.onyx.android.dr.reader.note.data.ReaderNotePageNameMap;
import com.raizlabs.android.dbflow.converter.TypeConverter;

/**
 * Created by zhuzeng on 9/16/16.
 */
public class ConverterPageMap  extends TypeConverter<String, ReaderNotePageNameMap> {

    @Override
    public String getDBValue(ReaderNotePageNameMap model) {
        return JSON.toJSONString(model);
    }

    @Override
    public ReaderNotePageNameMap getModelValue(String data) {
        return JSON.parseObject(data, ReaderNotePageNameMap.class);
    }

}
