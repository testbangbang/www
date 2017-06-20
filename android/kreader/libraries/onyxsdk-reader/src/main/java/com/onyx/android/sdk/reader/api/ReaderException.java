package com.onyx.android.sdk.reader.api;

/**
 * Created by zhuzeng on 10/2/15.
 */
public class ReaderException extends Exception {

    private int code;
    public static final int PASSWORD_REQUIRED = 1;
    public static final int INVALID_PLUGIN = 2;
    public static final int COULD_NOT_OPEN = 3;
    public static final int NULL_REQUEST = 4;
    public static final int EXCEED_LAST_PAGE = 5;
    public static final int EXCEED_FIRST_PAGE = 6;
    public static final int OUT_OF_RANGE = 7;
    public static final int UNKNOWN_EXCEPTION = 8;
    public static final int FILE_READ_ONLY = 9;
    public static final int ACTIVATION_FAILED = 0xff;

    public ReaderException(int theCode, String theMessage) {
        super(theMessage);
        code = theCode;
    }

    public ReaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReaderException(Throwable cause) {
        super(cause);
    }

    public int getCode() {
        return code;
    }

    static public ReaderException invalidPlugin() {
        return exceptionFromCode(INVALID_PLUGIN, "Invalid plugin.");
    }

    static public ReaderException createException(Exception e) {
        return exceptionFromCode(UNKNOWN_EXCEPTION, e.toString());
    }

    static public ReaderException passwordRequired() {
        return exceptionFromCode(PASSWORD_REQUIRED, "Password required.");
    }

    static public ReaderException cannotOpen() {
        return exceptionFromCode(COULD_NOT_OPEN, "Could not open document.");
    }

    static public ReaderException fileReadOnly() {
        return exceptionFromCode(FILE_READ_ONLY, "This file is read only.");
    }

    static public ReaderException exceptionFromCode(int code, String errorMessage) {
        return new ReaderException(code, errorMessage);
    }

    static public ReaderException nullRequest() {
        return exceptionFromCode(NULL_REQUEST, "Null request.");
    }

    static public ReaderException exceedLastPage() {
        return exceptionFromCode(EXCEED_LAST_PAGE, "Exceed last page.");
    }

    static public ReaderException exceedFirstPage(){
        return exceptionFromCode(EXCEED_FIRST_PAGE, "Exceed first page.");
    }

    static public ReaderException outOfRange() {
        return exceptionFromCode(OUT_OF_RANGE, "Out of range.");
    }

    static public ReaderException activationFailed(final String message) {
        return exceptionFromCode(ACTIVATION_FAILED, message);
    }

}
