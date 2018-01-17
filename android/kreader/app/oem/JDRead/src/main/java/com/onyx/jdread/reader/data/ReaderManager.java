package com.onyx.jdread.reader.data;


import android.content.Context;

import com.onyx.jdread.reader.common.DocumentInfo;

import java.util.HashMap;

/**
 * Created by zhuzeng on 10/5/15.
 * Map between document and corresponding reader.
 */
public class ReaderManager {

    static HashMap<String, Reader> readerHashMap = new HashMap<String, Reader>();

    public static boolean releaseReader(final DocumentInfo documentInfo) {
        if (readerHashMap.containsKey(documentInfo.getBookSingleFlags())) {
            readerHashMap.remove(documentInfo.getBookSingleFlags());
            return true;
        }
        return false;
    }

    public static Reader getReader(final DocumentInfo documentInfo, Context context) {
        return new Reader(documentInfo,context);
    }
}
