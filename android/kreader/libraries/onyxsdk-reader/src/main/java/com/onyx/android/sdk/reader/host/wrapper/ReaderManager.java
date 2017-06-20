package com.onyx.android.sdk.reader.host.wrapper;

import java.util.HashMap;

/**
 * Created by zhuzeng on 10/5/15.
 * Map between document and corresponding reader.
 */
public class ReaderManager {

    static HashMap<String, Reader> readerHashMap = new HashMap<String, Reader>();

    public static boolean releaseReader(final String path) {
        if (readerHashMap.containsKey(path)) {
            readerHashMap.remove(path);
            return true;
        }
        return false;
    }

    public static Reader getReader(final String path) {
        Reader reader = readerHashMap.get(path);
        if (reader == null) {
            reader = new Reader();
            readerHashMap.put(path, reader);
        }
        return reader;
    }

}
