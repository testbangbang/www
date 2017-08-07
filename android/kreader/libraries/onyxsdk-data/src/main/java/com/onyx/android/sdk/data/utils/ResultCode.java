package com.onyx.android.sdk.data.utils;

import java.io.Serializable;

/**
 * Created by suicheng on 2016/10/14.
 */
public class ResultCode implements Serializable {
    public static final String CODE = "code";
    public static final String MESSAGE = "message";

    public static final int SESSION_TOKEN_INVALID = 42010011;
    public static final int SESSION_TOKEN_MISSING = 42010010;

    public int code;
    public String message;

    public boolean isTokenInvalid() {
        return code == SESSION_TOKEN_INVALID || code == SESSION_TOKEN_MISSING;
    }
}
