package com.onyx.android.edu.utils;

import com.alibaba.fastjson.JSON;

import java.lang.reflect.Type;
import java.util.List;

/**
 * json数据处理类
 * Created by ming on 15/8/25.
 */
public class JsonUtils {

    public static <T> T toBean(byte[] json, Class<T> type) {
        T obj = null;
        try {
            String info = new String(json, "UTF-8");
            obj = JSON.parseObject(info, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static <T> T toBean(byte[] json, Type type) {
        T obj = null;
        try {
            String info = new String(json, "UTF-8");
            obj = JSON.parseObject(info, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static <T> T toBean(String json, Type type) {
        T obj;
        obj = JSON.parseObject(json, type);
        return obj;
    }

    public static String toJson(Object object) {
        String result = JSON.toJSONString(object);
        return result;
    }

    public static <T> List<T> toList(String json,Type type){
        List<T> list = JSON.parseObject(json, type);
        return list;
    }
}
