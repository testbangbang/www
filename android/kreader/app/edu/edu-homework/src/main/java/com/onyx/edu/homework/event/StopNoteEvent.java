package com.onyx.edu.homework.event;

/**
 * Created by lxm on 2017/12/14.
 */

public class StopNoteEvent {

    public boolean finishAfterSave;

    public StopNoteEvent(boolean finishAfterSave) {
        this.finishAfterSave = finishAfterSave;
    }
}
