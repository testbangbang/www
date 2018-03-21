package com.onyx.android.sdk.reader.api;

/**
 * Created by joy on 3/20/18.
 */

public class ReaderError extends Error {

    public static final int NET_NOVEL_CHAPTER_NOT_FOUND = 100;

    public int code;

    public ReaderError(int code, String message) {
        super(message);
        this.code = code;
    }

    static public ReaderError errorFromCode(int code, String errorMessage) {
        return new ReaderError(code, errorMessage);
    }

    static public ReaderError netNovelChapterNotFound(final String chapterId) {
        return errorFromCode(NET_NOVEL_CHAPTER_NOT_FOUND, chapterId);
    }

}
