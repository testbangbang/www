package com.onyx.kreader.note.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.kreader.note.NoteManager;
import com.onyx.kreader.note.request.FlushShapeListRequest;
import com.onyx.kreader.ui.actions.BaseAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by zhuzeng on 9/22/16.
 */
public class FlushNoteAction extends BaseAction {

    private boolean render;
    private boolean save;

    public FlushNoteAction(boolean r, boolean s) {
        render = r;
        save = s;
    }

    public void execute(final ReaderDataHolder readerDataHolder) {
        execute(readerDataHolder, null);
    }

    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        final NoteManager noteManager = readerDataHolder.getNoteManager();
        final FlushShapeListRequest flushRequest = new FlushShapeListRequest(noteManager.detachShapeStash(), 0, render, save);
        noteManager.submit(readerDataHolder.getContext(), flushRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                BaseCallback.invoke(callback, request, e);
            }
        });
    }

}
