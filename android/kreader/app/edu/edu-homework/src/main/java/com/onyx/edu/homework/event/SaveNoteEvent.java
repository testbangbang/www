package com.onyx.edu.homework.event;

/**
 * Created by lxm on 2017/12/14.
 */

public class SaveNoteEvent {

    public boolean finishAfterSave;
    public boolean showLoading;

    public SaveNoteEvent(boolean finishAfterSave) {
        this.finishAfterSave = finishAfterSave;
    }

    public SaveNoteEvent(boolean finishAfterSave, boolean showLoading) {
        this.finishAfterSave = finishAfterSave;
        this.showLoading = showLoading;
    }
}
