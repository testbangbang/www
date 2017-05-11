package com.onyx.edu.reader.note.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.edu.reader.note.request.ChangeShapeRequest;
import com.onyx.edu.reader.ui.actions.BaseAction;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

/**
 * Created by zhuzeng on 9/23/16.
 */
public class ChangeNoteShapeAction extends BaseAction {

    private int shape;
    public ChangeNoteShapeAction(int s) {
        shape = s;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback baseCallback) {
        final ChangeShapeRequest changeShapeRequest = new ChangeShapeRequest(shape);
        readerDataHolder.getNoteManager().submit(readerDataHolder.getContext(), changeShapeRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                readerDataHolder.getNoteManager().ensureContentRendered();
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }

}
