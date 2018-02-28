package com.onyx.android.note.event;

/**
 * Created by lxm on 2018/2/26.
 */

public class SaveDocumentEvent {

    public boolean closeAfterSave;
    public String title;

    public SaveDocumentEvent(boolean closeAfterSave, String title) {
        this.closeAfterSave = closeAfterSave;
        this.title = title;
    }
}
