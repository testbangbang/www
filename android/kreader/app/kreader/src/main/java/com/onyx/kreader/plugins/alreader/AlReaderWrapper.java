package com.onyx.kreader.plugins.alreader;

import com.neverland.engbook.bookobj.AlBookEng;
import com.onyx.android.sdk.utils.StringUtils;

/**
 * Created by zhuzeng on 29/10/2016.
 */

public class AlReaderWrapper {

    static public long NO_ERROR = 0;
    static public long ERROR_UNKNOWN = 1;
    static public long ERROR_FILE_NOT_FOUND = 2;
    static public long ERROR_FILE_INVALID = 3;
    static public long ERROR_PASSWORD_INVALID = 4;
    static public long ERROR_SECURITY = 5;
    static public long ERROR_PAGE_NOT_FOUND = 6;

    private AlBookEng eng;

    public AlReaderWrapper() {
        eng = new AlBookEng();
    }

    public long openDocument(final String path, final String password) {
        return 0;
    }

    public void closeDocument() {

    }

    public String metadataString(final String tag) {
        byte [] data  = new byte[4096];
        return StringUtils.utf16le(data).trim();
    }


}
