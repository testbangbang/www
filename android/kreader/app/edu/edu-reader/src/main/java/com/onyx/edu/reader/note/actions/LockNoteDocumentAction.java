package com.onyx.edu.reader.note.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.edu.reader.note.request.LockNoteDocumentRequest;
import com.onyx.edu.reader.ui.actions.BaseAction;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;
import com.onyx.edu.reader.ui.events.ActiveFormHandlerEvent;

/**
 * Created by lxm on 2017/8/22.
 */

public class LockNoteDocumentAction extends BaseAction {

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback baseCallback) {
        LockNoteDocumentRequest documentRequest = new LockNoteDocumentRequest();
        readerDataHolder.getNoteManager().submit(readerDataHolder.getContext(), documentRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                StopNoteActionChain stopNoteActionChain = new StopNoteActionChain(true, true, true, false, false, false);
                stopNoteActionChain.execute(readerDataHolder, null);
                readerDataHolder.getEventBus().post(new ActiveFormHandlerEvent());
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }
}
