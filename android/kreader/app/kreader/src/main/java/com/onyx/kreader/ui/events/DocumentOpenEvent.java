package com.onyx.kreader.ui.events;

import android.content.Context;

/**
 * Created by zhuzeng on 7/29/16.
 */
public class DocumentOpenEvent {
    private Context context;
    private String path;
    private String md5;


    public DocumentOpenEvent(final Context c, final String p, final String uniqueId) {
        context = c;
        path = p;
        md5 = uniqueId;
    }

    public final String getPath() {
        return path;
    }

    public String getMd5() {
        return md5;
    }

    public Context getContext() {
        return context;
    }
}
