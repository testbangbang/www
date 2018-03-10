package com.onyx.android.note.note;

import com.onyx.android.note.event.data.UpdateNoteDrawingArgsEvent;
import com.onyx.android.sdk.scribble.data.NoteDrawingArgs;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by lxm on 2018/2/28.
 */

public class DrawDataHolder {

    private EventBus eventBus;
    private NoteDrawingArgs drawingArgs = new NoteDrawingArgs();

    public DrawDataHolder(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public void subscribe() {
        getEventBus().register(this);
    }

    public void unSubscribe() {
        getEventBus().unregister(this);
    }

    @Subscribe
    public void onUpdateNoteDrawingArgs(UpdateNoteDrawingArgsEvent event) {
        drawingArgs.copyFrom(event.getDrawingArgs());
    }

    public NoteDrawingArgs getDrawingArgs() {
        return drawingArgs;
    }

    public void setStrokeWidth(float width) {
        getDrawingArgs().strokeWidth = width;
    }

    public void setCurrentShapeType(int newShape) {
        getDrawingArgs().setCurrentShapeType(newShape);
    }
}
