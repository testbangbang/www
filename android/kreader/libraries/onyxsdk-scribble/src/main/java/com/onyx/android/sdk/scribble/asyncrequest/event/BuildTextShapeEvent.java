package com.onyx.android.sdk.scribble.asyncrequest.event;

import com.onyx.android.sdk.scribble.view.LinedEditText;

/**
 * Created by lxm on 2017/8/30.
 */

public class BuildTextShapeEvent {

    private LinedEditText spanTextView;
    private String text;

    public LinedEditText getSpanTextView() {
        return spanTextView;
    }

    public String getText() {
        return text;
    }

    public BuildTextShapeEvent(LinedEditText spanTextView, String text) {
        this.spanTextView = spanTextView;
        this.text = text;
    }
}
