package com.onyx.android.note.action;

import android.support.annotation.NonNull;

import com.onyx.android.note.common.base.BaseNoteAction;
import com.onyx.android.note.event.RefreshDrawScreenEvent;
import com.onyx.android.sdk.note.NoteManager;
import com.onyx.android.sdk.note.request.RendererToScreenRequest;
import com.onyx.android.sdk.rx.RxCallback;

/**
 * Created by lxm on 2018/2/27.
 */

public class RefreshDrawScreenAction extends BaseNoteAction {

    private boolean redrawBitmap;

    public RefreshDrawScreenAction(NoteManager noteManager) {
        super(noteManager);
    }

    public RefreshDrawScreenAction setRedrawBitmap(boolean redrawBitmap) {
        this.redrawBitmap = redrawBitmap;
        return this;
    }

    @Override
    public void execute(final RxCallback rxCallback) {
        RendererToScreenRequest request = new RendererToScreenRequest(getNoteManager());
        request.setRedrawBitmap(redrawBitmap);
        getNoteManager().getRxManager().enqueue(request, new RxCallback<RendererToScreenRequest>() {
            @Override
            public void onNext(@NonNull RendererToScreenRequest rendererToScreenRequest) {
                RxCallback.onNext(rxCallback, rendererToScreenRequest);
                getNoteManager().post(new RefreshDrawScreenEvent(true));
            }
        });
    }
}
