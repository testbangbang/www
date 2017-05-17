package com.onyx.edu.reader.note.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.edu.reader.note.NoteManager;
import com.onyx.edu.reader.note.request.ChangeShapeRequest;
import com.onyx.edu.reader.ui.actions.BaseAction;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;
import com.onyx.edu.reader.ui.handler.HandlerManager;

/**
 * Created by zhuzeng on 9/28/16.
 */

public class StartErasingAction extends BaseAction {

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        readerDataHolder.getHandlerManager().setActiveProvider(HandlerManager.SCRIBBLE_PROVIDER);
        final NoteManager noteManager = readerDataHolder.getNoteManager();
        final ChangeShapeRequest changeShapeRequest = new ChangeShapeRequest(ShapeFactory.SHAPE_ERASER);
        noteManager.submit(readerDataHolder.getContext(), changeShapeRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                BaseCallback.invoke(callback, request, e);
            }
        });
    }
}
