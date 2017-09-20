package com.onyx.android.sdk.scribble.api.event;

import com.onyx.android.sdk.scribble.view.LinedEditText;

/**
 * Created by lxm on 2017/8/30.
 */

public class UpdateLineLayoutCursorEvent {

    private LinedEditText spanTextView;

    public LinedEditText getSpanTextView() {
        return spanTextView;
    }

    public UpdateLineLayoutCursorEvent(LinedEditText spanTextView) {
        this.spanTextView = spanTextView;
    }
}
