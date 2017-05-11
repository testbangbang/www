package com.onyx.edu.reader.ui.events;

import android.content.Context;

/**
 * Created by zhuzeng on 08/01/2017.
 */

public class AnnotationEvent {

    private Context context;
    private String originText;
    private String userNote;

    public AnnotationEvent(final String t) {
        originText = t;
    }

    public AnnotationEvent(final Context c, final String t, final String note) {
        context = c;
        originText = t;
        userNote = note;
    }

    public static AnnotationEvent onAddAnnotation(final Context context, final String text, final String note) {
        final AnnotationEvent annotationEvent = new AnnotationEvent(context, text, note);
        return annotationEvent;
    }

    public String getOriginText() {
        return originText;
    }

    public String getUserNote() {
        return userNote;
    }

    public Context getContext() {
        return context;
    }
}
