package com.onyx.edu.reader.ui.events;

import android.content.Context;

/**
 * Created by zhuzeng on 9/23/16.
 */
public class DocumentCloseEvent {
    private Context context;

    public DocumentCloseEvent(final Context c) {
        context = c;
    }

    public Context getContext() {
        return context;
    }
}
