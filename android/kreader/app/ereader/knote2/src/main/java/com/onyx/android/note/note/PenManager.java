package com.onyx.android.note.note;

import android.util.Log;

import com.onyx.android.note.event.ClearAllFreeShapesEvent;
import com.onyx.android.note.event.OpenDocumentEvent;
import com.onyx.android.note.event.RefreshDrawScreenEvent;
import com.onyx.android.note.event.menu.BackgroundChangeEvent;
import com.onyx.android.note.event.menu.PenWidthChangeEvent;
import com.onyx.android.note.event.menu.TopMenuChangeEvent;
import com.onyx.android.note.event.menu.UndoRedoEvent;
import com.onyx.android.sdk.note.event.ResumeRawDrawingEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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

    public boolean isDialogShowed() {
        return dialogShowed;
    }

    private boolean shouldResume(boolean resumePen) {
        return resumePen && !isDialogShowed();
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
    }

    @Subscribe
    public void onRefreshDrawScreen(RefreshDrawScreenEvent event) {
        resumeRawDrawing(event.isResumePen());
    }

    private void resumeRawDrawing(boolean resumePen) {
        if (!shouldResume(resumePen)) {
            return;
        }
        getEventBus().post(new ResumeRawDrawingEvent());
    }

}
