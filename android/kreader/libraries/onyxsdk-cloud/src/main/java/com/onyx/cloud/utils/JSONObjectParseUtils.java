package com.onyx.cloud.utils;

import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.onyx.cloud.model.OnyxAccount;
import com.onyx.cloud.model.Product;

public class JSONObjectParseUtils {
    public static final String TAG_UPDATEDAT = "updatedAt";
    public static final String TAG_CREATEDAT = "createdAt";
    public static final String TAG_SESSIONTOKEN = "sessionToken";
    public static final String TAG_MAIN = "main";
    public static final String TAG_EMAIL = "email";
    public static final String TAG_DEVICEIDS = "deviceIds";
    public static final String TAG_CODE = "code";

    public static final int STATUSCODE_SUCCESS_RESPONSE = 200;
    public static final int STATUSCODE_ERROR_SERVER_NO_ACTION = 0;
    public static final int STATUSCODE_ERROR_BAD_REQUEST = 400;
    public static final int STATUSCODE_ERROR_UNAUTHORIZED = 401;
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

    public static OnyxAccount jsonObject2OnyxAccount(JSONObject jsonObject) {
        OnyxAccount onyxAccount = null;
        try {
            onyxAccount = JSON.parseObject(jsonObject.toString(), OnyxAccount.class);

            JSONObject mainObject = (JSONObject) jsonObject.get(TAG_MAIN);
            onyxAccount.email = mainObject.getString(TAG_EMAIL);

            if (jsonObject.getJSONArray(TAG_DEVICEIDS).length() > 0) {
                onyxAccount.isInstallationId = true;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return onyxAccount;
    }

    public static Product jsonObject2Product(JSONObject jsonObject) {
        Product product = null;
        product = JSON.parseObject(jsonObject.toString(), Product.class);
        return product;
    }

    public static String httpStatus(int statusCode, JSONObject errorResponse) throws JSONException {
        String errorCode = String.valueOf(ACCOUNT_ERROR_INCORRECT_PASSWORD);
        switch (statusCode) {
        case STATUSCODE_ERROR_SERVER_NO_ACTION:
            errorCode = String.valueOf(statusCode);
            break;
        case STATUSCODE_ERROR_BAD_REQUEST:
        case STATUSCODE_ERROR_UNAUTHORIZED:
            errorCode = String.valueOf(errorResponse.getInt(TAG_CODE));
            break;
        }
        return errorCode;
    }

}
