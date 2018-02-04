package com.onyx.jdread.main.common;

import com.alibaba.fastjson.JSON;
import com.raizlabs.android.dbflow.converter.TypeConverter;

import java.lang.reflect.ParameterizedType;

/**
 * Created by suicheng on 2018/2/1.
 */
public abstract class JSONConverter<Stub extends String, Model> extends TypeConverter<Stub, Model> {
    @Override
    public Stub getDBValue(Model model) {
        return (Stub) JSON.toJSONString(model);
    }

    @Override
    public Model getModelValue(Stub data) {
        Class<Model> entityClass = (Class<Model>) ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments()[1];
        return JSON.parseObject(data, entityClass);
    }
}
