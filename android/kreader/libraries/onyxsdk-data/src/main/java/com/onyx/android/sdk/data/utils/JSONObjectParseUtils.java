package com.onyx.android.sdk.data.utils;

import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.onyx.android.sdk.data.model.OnyxAccount;

import java.lang.reflect.Type;
import java.util.List;

public class JSONObjectParseUtils {
    public static final String TAG_UPDATEDAT = "updatedAt";
    public static final String TAG_CREATEDAT = "createdAt";
    public static final String TAG_SESSIONTOKEN = "sessionToken";
    public static final String TAG_MAIN = "main";
    public static final String TAG_EMAIL = "email";
    public static final String TAG_DEVICE_IDS = "deviceIds";
    public static final String TAG_CODE = "code";
    public static final String TAG_MESSAGE = "message";

    public static final int STATUS_CODE_SUCCESS_RESPONSE = 200;
    public static final int STATUS_CODE_ERROR_SERVER_NO_ACTION = 0;
    public static final int STATUS_CODE_ERROR_BAD_REQUEST = 400;
    public static final int STATUS_CODE_ERROR_UNAUTHORIZED = 401;
    public static final int ACCOUNT_ERROR_EXIST = 404;
    public static final int ACCOUNT_ERROR_INCORRECT_PASSWORD = 405;
    public static final int ACCOUNT_ERROR_NOT_EXIST = 406;

    public static class deviceId {
        public static final String machineIdentifier = "machineIdentifier";
        public static final String timestamp = "timestamp";
        public static final String timeSecond = "timeSecond";
        public static final String time = "time";
        public static final String date = "date";
    }

    public static OnyxAccount parseOnyxAccount(String jsonString) {
        OnyxAccount onyxAccount = null;
        try {
            onyxAccount = JSON.parseObject(jsonString, OnyxAccount.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return onyxAccount;
    }

    public static String httpStatus(int statusCode, JSONObject errorResponse) throws JSONException {
        return errorResponse.getInt(TAG_CODE) + "," + errorResponse.getInt(TAG_MESSAGE);
    }

    public static <T> T parseObject(String json, Class<T> cls, Feature... features) {
        try {
            return JSON.parseObject(json, cls, features);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static public <T> T parseObject(String json, TypeReference<T> typeReference, Feature... features) {
        try {
            return JSON.parseObject(json, typeReference, features);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

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

    public static <T> List<T> toList(String json, Type type){
        List<T> list = JSON.parseObject(json, type);
        return list;
    }
}
