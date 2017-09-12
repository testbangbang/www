package com.onyx.android.sdk.scribble.asyncrequest.event;

import com.onyx.android.sdk.scribble.view.LinedEditText;

/**
 * Created by lxm on 2017/8/30.
 */

public class UpdateLineLayoutArgsEvent {

    private LinedEditText spanTextView;

    public LinedEditText getSpanTextView() {
        return spanTextView;
    }

    public UpdateLineLayoutArgsEvent(LinedEditText spanTextView) {
        this.spanTextView = spanTextView;
    }
}