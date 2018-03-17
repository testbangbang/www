package com.onyx.jdread.shop.cloud.entity.jdbean;

import com.onyx.jdread.main.common.Constants;

import java.io.Serializable;

/**
 * Created by jackdeng on 2018/2/8.
 */

public class BaseResultBean implements Serializable {
    public int result_code;
    public String message;

    public static boolean checkSuccess(BaseResultBean resultBean) {
        return resultBean != null && checkSuccess(resultBean.result_code);
    }

    public static boolean checkSuccess(int resultCode) {
        return Integer.valueOf(Constants.RESULT_CODE_SUCCESS) == resultCode;
    }
}
