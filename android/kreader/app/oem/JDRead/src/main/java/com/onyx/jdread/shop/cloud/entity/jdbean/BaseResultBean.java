package com.onyx.jdread.shop.cloud.entity.jdbean;

import java.io.Serializable;

/**
 * Created by jackdeng on 2018/2/8.
 */

public class BaseResultBean implements Serializable {
    public int result_code;
    public String message;

    public static boolean checkSuccess(BaseResultBean resultBean) {
        return resultBean != null && resultBean.result_code == 0;
    }
}
