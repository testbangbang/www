package com.onyx.android.sdk.data.common;

import com.onyx.android.sdk.data.utils.ResultCode;

import java.io.File;


/**
 * Created by suicheng on 2017/5/23.
 */
public class ContentException extends Exception {
    private int code;

    public static final int URL_INVALID_EXCEPTION = 1;
    public static final int FILE_PATH_EXCEPTION = 2;
    public static final int NETWORK_EXCEPTION = 3;
    public static final int TOKEN_EXCEPTION = 4;
    public static final int IO_EXCEPTION = 5;
    public static final int UNKNOWN_EXCEPTION = 0xffff;

    public static final int CLOUD_TOKEN_EXCEPTION = 401;
    public static final int CLOUD_NO_FOUND = 404;
    public static final int CLOUD_INTERNAL_SERVER_ERROR = 500;

    public static final String URL_INVALID_EXCEPTION_MESSAGE = "Url is invalid.";
    public static final String FILE_PATH_EXCEPTION_MESSAGE = "File path is invalid.";
    public static final String NETWORK_EXCEPTION_MESSAGE = "Network is exception.";
    public static final String UNKNOWN_EXCEPTION_MESSAGE = "Unknown exception.";
    public static final String TOKEN_EXCEPTION_MESSAGE = "Token exception.";

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
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
            return UnKnowException();
        }
        String exceptionMessage = resultCode.message;
        int code = resultCode.code;
        if (exceptionMessage == null) {
            exceptionMessage = code + "," + NETWORK_EXCEPTION_MESSAGE;
        }
        return exceptionFromCode(code, exceptionMessage);
    }

    static public ContentException UrlInvalidException() {
        return exceptionFromCode(URL_INVALID_EXCEPTION, URL_INVALID_EXCEPTION_MESSAGE);
    }

    static public ContentException FilePathInvalidException() {
        return exceptionFromCode(FILE_PATH_EXCEPTION, FILE_PATH_EXCEPTION_MESSAGE);
    }

    static public ContentException UnKnowException() {
        return exceptionFromCode(UNKNOWN_EXCEPTION, UNKNOWN_EXCEPTION_MESSAGE);
    }

    static public ContentException TokenException() {
        return exceptionFromCode(TOKEN_EXCEPTION, TOKEN_EXCEPTION_MESSAGE);
    }

    public static class NetworkException extends ContentException {
        public NetworkException(Exception e) {
            super(e);
        }
    }

    public static class CloudException extends ContentException {

        public CloudException(ResultCode resultCode) {
            super(exceptionFromResultCode(resultCode));
            setCode(resultCode == null ? UNKNOWN_EXCEPTION : resultCode.code);
        }
    }

    public static class FileException extends ContentException {
        public FileException(int code, String msg) {
            super(code, msg);
        }
    }

    public static class FileAskForReplaceException extends FileException {
        public File from, to;

        public FileAskForReplaceException(File from, File to) {
            super(IO_EXCEPTION, "FileAskForReplaceException");

            this.from = from;
            this.to = to;
        }
    }

    public static class FileCopyException extends FileException {
        public File from, to;

        public FileCopyException(File from, File to) {
            super(IO_EXCEPTION, "FileCopyException");

            this.from = from;
            this.to = to;
        }
    }

    public static class FileDeleteException extends FileException {
        public File file;
        public FileDeleteException(File file) {
            super(IO_EXCEPTION, "FileDeleteException");
            this.file = file;
        }
    }

    public static class FileCreateException extends FileException {
        public File file;
        public FileCreateException(File file) {
            super(IO_EXCEPTION, "FileCreateException");
            this.file = file;
        }
    }

    public static boolean isNetworkException(Throwable throwable) {
        return throwable instanceof NetworkException;
    }

    public static boolean isCloudException(Throwable throwable) {
        return throwable instanceof CloudException;
    }

    public boolean isCloudNotFound() {
        return getCode() == CLOUD_NO_FOUND;
    }

    public boolean isCloudServerError() {
        return getCode() == CLOUD_INTERNAL_SERVER_ERROR;
    }

    public boolean isCloudTokenException() {
        return getCode() == CLOUD_TOKEN_EXCEPTION;
    }
}
