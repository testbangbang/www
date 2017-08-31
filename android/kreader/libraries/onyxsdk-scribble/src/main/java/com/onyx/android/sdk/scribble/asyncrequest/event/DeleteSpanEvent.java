package com.onyx.android.sdk.scribble.asyncrequest.event;

/**
 * Created by lxm on 2017/8/30.
 */

public class DeleteSpanEvent {

    private boolean resume;

    public boolean isResume() {
        return resume;
    }

    public DeleteSpanEvent(boolean resume) {
        this.resume = resume;
    }
}
