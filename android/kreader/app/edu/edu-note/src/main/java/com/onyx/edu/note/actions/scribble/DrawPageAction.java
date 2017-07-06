package com.onyx.edu.note.actions.scribble;

import android.view.SurfaceView;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.edu.note.NoteManager;
import com.onyx.edu.note.actions.BaseNoteAction;
import com.onyx.edu.note.request.scribble.DrawPageRequest;

/**
 * Created by solskjaer49 on 2017/7/6 11:14.
 */

public class DrawPageAction extends BaseNoteAction {
    private SurfaceView mSurfaceView;

    public DrawPageAction(SurfaceView mSurfaceView) {
        this.mSurfaceView = mSurfaceView;
    }

    @Override
    public void execute(NoteManager noteManager, BaseCallback callback) {
        DrawPageRequest drawPageRequest = new DrawPageRequest(mSurfaceView, noteManager.getViewBitmap(), noteManager.getDirtyShape());
        noteManager.submitRequest(drawPageRequest, callback);
    }
}
