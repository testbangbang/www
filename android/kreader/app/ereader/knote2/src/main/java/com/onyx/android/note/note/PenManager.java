package com.onyx.android.note.note;

import com.onyx.android.note.NoteDataBundle;
import com.onyx.android.note.event.AddShapesEvent;
import com.onyx.android.note.event.ClearAllFreeShapesEvent;
import com.onyx.android.note.event.OpenDocumentEvent;
import com.onyx.android.note.event.RefreshDrawScreenEvent;
import com.onyx.android.note.event.menu.BackgroundChangeEvent;
import com.onyx.android.note.event.menu.PenWidthChangeEvent;
import com.onyx.android.note.event.menu.TopMenuChangeEvent;
import com.onyx.android.note.event.menu.UndoRedoEvent;
import com.onyx.android.note.handler.HandlerManager;
import com.onyx.android.sdk.note.NoteManager;
import com.onyx.android.sdk.note.event.RawDrawingRenderEnabledEvent;
import com.onyx.android.sdk.note.event.ResumeRawDrawingEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by lxm on 2018/2/28.
 */

public class PenManager {

    private EventBus eventBus;
    private boolean dialogShowed = false;

    public PenManager(EventBus eventBus) {
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

    private boolean isDialogShowed() {
        return dialogShowed;
    }

    private boolean shouldResume(boolean resumePen) {
        return resumePen && !isDialogShowed();
    }

    private boolean shouldRawRender(boolean render) {
        return render || !getHandlerManager().inEraseProvider();
    }

    @Subscribe
    public void onTopMenuChange(TopMenuChangeEvent event) {
        resumeRawDrawing(event.isResumePen());
    }

    @Subscribe
    public void onPenWidthChange(PenWidthChangeEvent event) {
        resumeRawDrawing(event.isResumePen());
    }

    @Subscribe
    public void onBackgroundChange(BackgroundChangeEvent event) {
        resumeRawDrawing(event.isResumePen());
    }

    @Subscribe
    public void onClearAllFreeShapes(ClearAllFreeShapesEvent event) {
        resumeRawDrawing(event.isResumePen());
    }

    @Subscribe
    public void onOpenDocument(OpenDocumentEvent event) {
        resumeRawDrawing(event.isResumePen());
    }

    @Subscribe
    public void onUndoRedo(UndoRedoEvent event) {
        resumeRawDrawing(event.isResumePen());
        setRawDrawingRenderEnabled(event.isRawRenderEnable());
    }

    @Subscribe
    public void onRefreshDrawScreen(RefreshDrawScreenEvent event) {
        resumeRawDrawing(event.isResumePen());
    }

    @Subscribe
    public void onAddShapes(AddShapesEvent event) {
        resumeRawDrawing(event.isResumePen());
        setRawDrawingRenderEnabled(event.isRawRenderEnable());
    }

    private void setRawDrawingRenderEnabled(boolean enabled) {
        getNoteManager().post(new RawDrawingRenderEnabledEvent(shouldRawRender(enabled)));
    }

    private void resumeRawDrawing(boolean resumePen) {
        if (!shouldResume(resumePen)) {
            return;
        }
        getEventBus().post(new ResumeRawDrawingEvent());
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
