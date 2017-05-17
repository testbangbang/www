package com.onyx.edu.reader.note.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.edu.reader.note.NoteManager;
import com.onyx.edu.reader.note.request.RestoreShapeRequest;
import com.onyx.edu.reader.ui.actions.BaseAction;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

/**
 * Created by zhuzeng on 10/9/16.
 */

public class RestoreShapeAction extends BaseAction {

    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        final NoteManager noteManager = readerDataHolder.getNoteManager();
        final RestoreShapeRequest noteRequest = new RestoreShapeRequest();

        noteManager.submit(readerDataHolder.getContext(), noteRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                BaseCallback.invoke(callback, request, e);
            }
        });
    }

}
