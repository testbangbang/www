package com.onyx.android.sdk.data.common;

import com.onyx.android.sdk.data.utils.ResultCode;


/**
 * Created by suicheng on 2017/5/23.
 */
public class ContentException extends Exception {
    private int code;

    public static final int URL_INVALID_EXCEPTION = 1;
    public static final int FILE_PATH_EXCEPTION = 2;
    public static final int NETWORK_EXCEPTION = 3;
    public static final int UNKNOWN_EXCEPTION = 0xffff;

    public static final String URL_INVALID_EXCEPTION_MESSAGE = "Url is invalid.";
    public static final String FILE_PATH_EXCEPTION_MESSAGE = "File path is invalid.";
    public static final String NETWORK_EXCEPTION_MESSAGE = "Network is exception.";

    public int getCode() {
        return code;
    }

    public ContentException(int theCode, String theMessage) {
        super(theMessage);
        code = theCode;
    }

    public ContentException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContentException(Throwable cause) {
        super(cause);
    }

    static public ContentException createException(Exception e) {
        return exceptionFromCode(UNKNOWN_EXCEPTION, e.toString());
    }

    static public ContentException exceptionFromCode(int code, String errorMessage) {
        return new ContentException(code, errorMessage);
    }

    static public ContentException exceptionFromResultCode(ResultCode resultCode) {
        if (resultCode == null) {
            return NetworkException();
        }
        String exceptionMessage = resultCode.message;
        int code = resultCode.code;
        if (exceptionMessage == null) {
            exceptionMessage = NETWORK_EXCEPTION_MESSAGE;
        }
        return exceptionFromCode(code, exceptionMessage);
    }

    static public ContentException UrlInvalidException() {
        return exceptionFromCode(URL_INVALID_EXCEPTION, URL_INVALID_EXCEPTION_MESSAGE);
    }

    static public ContentException FilePathInvalidException() {
        return exceptionFromCode(FILE_PATH_EXCEPTION, FILE_PATH_EXCEPTION_MESSAGE);
    }

    static public ContentException NetworkException() {
        return exceptionFromCode(NETWORK_EXCEPTION, NETWORK_EXCEPTION_MESSAGE);
    }
}
