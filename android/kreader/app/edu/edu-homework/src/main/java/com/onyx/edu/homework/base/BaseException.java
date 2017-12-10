package com.onyx.edu.homework.base;

/**
 * Created by lxm on 2017/10/19.
 */

public class BaseException extends Exception {

    public static final int UNKNOWN_ERROR_CODE = 0xffff;
    public static final String UNKNOWN_ERROR_MESSAGE = "unknown_error";

    public int code;
    public String message;

    public BaseException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public BaseException(int code) {
        this.code = code;
    }

    public BaseException(String message) {
        this.message = message;
    }

    public BaseException(Throwable cause) {
        super(cause);
        message = cause.getMessage();
    }

    public String getErrorMessage() {
        if (message == null || message.isEmpty()) {
            return getMessage();
        }
        return message;
    }
}
