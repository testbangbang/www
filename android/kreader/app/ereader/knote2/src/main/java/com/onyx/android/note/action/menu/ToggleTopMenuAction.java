package com.onyx.android.note.action.menu;

import android.support.annotation.NonNull;

import com.onyx.android.note.common.base.BaseNoteAction;
import com.onyx.android.note.event.PenEvent;
import com.onyx.android.note.event.menu.CheckMenuRectEvent;
import com.onyx.android.note.note.menu.NoteMenuModel;
import com.onyx.android.sdk.note.NoteManager;
import com.onyx.android.sdk.note.request.RendererToScreenRequest;
import com.onyx.android.sdk.rx.RxCallback;

/**
 * Created by lxm on 2018/2/28.
 */

public class ToggleTopMenuAction extends BaseNoteAction {

    private NoteMenuModel menuModel;

    public ToggleTopMenuAction(NoteManager noteManager, NoteMenuModel menuModel) {
        super(noteManager);
        this.menuModel = menuModel;
    }

    @Override
    public void execute(final RxCallback rxCallback) {
        RendererToScreenRequest request = new RendererToScreenRequest(getNoteManager());
        request.setRedrawBitmap(false);
        getNoteManager().getRxManager().enqueue(request, new RxCallback<RendererToScreenRequest>() {
            @Override
            public void onNext(@NonNull RendererToScreenRequest rendererToScreenRequest) {
                menuModel.toggle();
                getNoteManager().post(new CheckMenuRectEvent());
                getNoteManager().post(PenEvent.resumeDrawingRender());
                RxCallback.onNext(rxCallback, rendererToScreenRequest);
            }
        });
    }
}
