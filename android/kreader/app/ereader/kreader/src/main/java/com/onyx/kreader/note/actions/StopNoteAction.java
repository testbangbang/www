package com.onyx.kreader.note.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.kreader.note.request.ChangeShapeRequest;
import com.onyx.kreader.note.request.StopNoteRequest;
import com.onyx.kreader.ui.actions.BaseAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by zhuzeng on 20/10/2016.
 */

public class StopNoteAction extends BaseAction {

    private boolean stop = true;

    public StopNoteAction() {
    }

    public StopNoteAction(boolean stop) {
        this.stop = stop;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback baseCallback) {
        final StopNoteRequest stopNoteRequest = new StopNoteRequest(stop);
        readerDataHolder.getNoteManager().submit(readerDataHolder.getContext(), stopNoteRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }

}
