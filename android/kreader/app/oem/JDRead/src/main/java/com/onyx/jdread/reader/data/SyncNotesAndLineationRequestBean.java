package com.onyx.jdread.reader.data;

import java.util.Map;

import okhttp3.RequestBody;

/**
 * Created by li on 2018/3/20.
 */

public class SyncNotesAndLineationRequestBean {
    public long bookId;
    public Map<String, String> baseInfoMap;
    public RequestBody body;
}
