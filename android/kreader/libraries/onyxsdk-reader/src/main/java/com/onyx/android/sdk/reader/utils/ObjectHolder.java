package com.onyx.android.sdk.reader.utils;

/**
 * Created by zhuzeng on 10/16/15.
 */
public class ObjectHolder<T> {

    private T object = null;

    public ObjectHolder() {
    }

    public ObjectHolder(T v) {
        object = v;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T v) {
        object = v;
    }

}
