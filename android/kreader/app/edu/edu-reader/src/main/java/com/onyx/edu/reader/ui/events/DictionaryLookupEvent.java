package com.onyx.edu.reader.ui.events;

import android.content.Context;

/**
 * Created by zhuzeng on 08/01/2017.
 */

public class DictionaryLookupEvent {
    private Context context;
    private String text;

    public DictionaryLookupEvent(final Context c, final String t) {
        context = c;
        text = t;
    }

    public static DictionaryLookupEvent create(final Context context, final String t) {
        final DictionaryLookupEvent event = new DictionaryLookupEvent(context, t);
        return event;
    }

    public String getText() {
        return text;
    }

    public Context getContext() {
        return context;
    }
}
