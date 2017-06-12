package com.onyx.edu.reader.note.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.edu.reader.note.NoteManager;
import com.onyx.edu.reader.note.request.LockFormShapesRequest;
import com.onyx.edu.reader.ui.actions.BaseAction;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

/**
 * Created by ming on 2017/6/8.
 */

public class LockFormShapesAction extends BaseAction {

    @Override
    public void execute(ReaderDataHolder readerDataHolder, BaseCallback baseCallback) {
        final NoteManager noteManager = readerDataHolder.getNoteManager();
        String documentId = readerDataHolder.getReader().getDocumentMd5();
        LockFormShapesRequest shapesSubmittedRequest = new LockFormShapesRequest(documentId);
        noteManager.submit(readerDataHolder.getContext(), shapesSubmittedRequest, baseCallback);
    }
}
