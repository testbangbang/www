package com.onyx.android.note.event.data;

import com.onyx.android.sdk.scribble.data.NoteDrawingArgs;

/**
 * Created by lxm on 2018/2/28.
 */

public class UpdateNoteDrawingArgsEvent {

    private NoteDrawingArgs drawingArgs;

    public UpdateNoteDrawingArgsEvent(NoteDrawingArgs drawingArgs) {
        this.drawingArgs = drawingArgs;
    }

    public NoteDrawingArgs getDrawingArgs() {
        return drawingArgs;
    }
}
