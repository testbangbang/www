package com.onyx.android.note.note;

import com.onyx.android.note.NoteDataBundle;
import com.onyx.android.note.event.DialogChangeEvent;
import com.onyx.android.note.event.KeyboardChangeEvent;
import com.onyx.android.note.event.PenEvent;
import com.onyx.android.note.handler.HandlerManager;
import com.onyx.android.sdk.note.NoteManager;
import com.onyx.android.sdk.note.event.RawDrawingRenderEnabledEvent;
import com.onyx.android.sdk.note.event.ResumeRawDrawingEvent;
import com.onyx.android.sdk.scribble.data.NoteDrawingArgs;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by lxm on 2018/2/28.
 */

public class PenEventHandler {

    private EventBus eventBus;
    private boolean dialogShowing;
    private boolean keyboardShowing;

    public PenEventHandler(EventBus eventBus) {
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

    private boolean isDialogShowing() {
        return dialogShowing;
    }

    public boolean isKeyboardShowing() {
        return keyboardShowing;
    }

    private boolean shouldResume(boolean resumePen) {
        return resumePen
                && !isDialogShowing()
                && !isKeyboardShowing()
                && inRawRenderProvider()
                && inRawRenderShapeType();
    }

    private boolean inRawRenderProvider() {
        return !getHandlerManager().inEraseOverlayProvider() &&
                !getHandlerManager().inNormalShapeProvider();
    }

    private boolean inRawRenderShapeType() {
        NoteDrawingArgs drawingArgs = getDataBundle().getDrawingArgs();
        int currentShapeType = drawingArgs.getCurrentShapeType();
        return ShapeFactory.isDFBShape(currentShapeType);
    }

    @Subscribe
    public void onKeyboardChange(KeyboardChangeEvent event) {
        keyboardShowing = event.show;
        resumeRawDrawingRender(!keyboardShowing);
    }

    @Subscribe
    public void onDialogChange(DialogChangeEvent event) {
        dialogShowing = event.show;
        resumeRawDrawingRender(!dialogShowing);
    }

    @Subscribe
    public void onPenEvent(PenEvent event) {
        resumeRawDrawingRender(event.isResumeDrawingRender());
    }

    private void resumeRawDrawingRender(boolean resumePen) {
        if (!shouldResume(resumePen)) {
            return;
        }
        getEventBus().post(new RawDrawingRenderEnabledEvent(resumePen));
    }

    private NoteDataBundle getDataBundle() {
        return NoteDataBundle.getInstance();
    }

    private NoteManager getNoteManager() {
        return getDataBundle().getNoteManager();
    }

    private HandlerManager getHandlerManager() {
        return getDataBundle().getHandlerManager();
    }

}
