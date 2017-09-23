package com.onyx.android.dr.reader.event;


import android.content.Context;

/**
 * Created by zhuzeng on 08/01/2017.
 */

public class TextSelectionEvent {
    private Context context;
    private String text;

    public TextSelectionEvent(final Context c, final String t) {
        context = c;
        text = t;
    }

    public static TextSelectionEvent onTextSelected(final Context context, final String text) {
        final TextSelectionEvent event = new TextSelectionEvent(context, text);
        return event;
    }

    public String getText() {
        return text;
    }

    public Context getContext() {
        return context;
    }
}
