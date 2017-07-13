package com.onyx.android.dr.bean;

/**
 * Created by zhuzeng on 5/29/15.
 */
public class DictionaryException extends Exception {
    public static final int PASSWORD_REQUIRED = 1;
    public static final int INVALID_PLUGIN = 2;
    public static final int COULD_NOT_OPEN = 3;
    public static final int NULL_REQUEST = 4;
    public static final int EXCEED_LAST_PAGE = 5;
    public static final int EXCEED_FIRST_PAGE = 6;
    public static final int OUT_OF_RANGE = 7;
    public static final int IO_EXCEPTION = 8;
    public static final int TARGET_NOT_EXISTED = 9;
    public static final int TARGET_NOT_READABLE = 10;
    public static final int EXEC_COMMAND_ERROR = 11;
    public static final int UNKNOWN_EXCEPTION = 0xffff;
    private int code;

    public DictionaryException(int theCode, String theMessage) {
        super(theMessage);
        code = theCode;
    }

    public DictionaryException(String message, Throwable cause) {
        super(message, cause);
    }

    public DictionaryException(Throwable cause) {
        super(cause);
    }

    static public DictionaryException createException(Exception e) {
        return exceptionFromCode(UNKNOWN_EXCEPTION, e.toString());
    }

    static public DictionaryException exceptionFromCode(int code, String errorMessage) {
        return new DictionaryException(code, errorMessage);
    }

    static public DictionaryException nullRequest() {
        return exceptionFromCode(NULL_REQUEST, "Null request.");
    }

    public int getCode() {
        return code;
    }
}
