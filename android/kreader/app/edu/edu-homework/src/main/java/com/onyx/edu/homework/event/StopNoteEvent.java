package com.onyx.edu.homework.event;

import com.onyx.android.sdk.common.request.BaseCallback;

/**
 * Created by lxm on 2017/12/14.
 */

public class StopNoteEvent {

    public boolean finishAfterSave;
    public BaseCallback callback;

    public StopNoteEvent(boolean finishAfterSave) {
        this.finishAfterSave = finishAfterSave;
    }

    public StopNoteEvent(boolean finishAfterSave, BaseCallback callback) {
        this.finishAfterSave = finishAfterSave;
        this.callback = callback;
    }
}
