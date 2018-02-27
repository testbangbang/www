package com.onyx.android.note.action;

import com.onyx.android.note.common.base.BaseNoteAction;
import com.onyx.android.sdk.note.NoteManager;
import com.onyx.android.sdk.note.request.RendererToScreenRequest;
import com.onyx.android.sdk.rx.RxCallback;

/**
 * Created by lxm on 2018/2/27.
 */

public class RendererToScreenAction extends BaseNoteAction {

    private boolean rebuildToBitmap;

    public RendererToScreenAction(NoteManager noteManager) {
        super(noteManager);
    }

    public RendererToScreenAction setRebuildToBitmap(boolean rebuildToBitmap) {
        this.rebuildToBitmap = rebuildToBitmap;
        return this;
    }

    @Override
    public void execute(RxCallback rxCallback) {
        RendererToScreenRequest request = new RendererToScreenRequest(getNoteManager());
        request.setRebuildToBitmap(rebuildToBitmap);
        getNoteManager().getRxManager().enqueue(request, rxCallback);
    }
}
