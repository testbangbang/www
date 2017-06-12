package com.onyx.kreader.note.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.kreader.note.request.ChangeShapeRequest;
import com.onyx.kreader.note.request.ChangeStrokeWidthRequest;
import com.onyx.kreader.ui.actions.BaseAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by zhuzeng on 9/23/16.
 */
public class ChangeStrokeWidthAction extends BaseAction {

    private float width;
    private boolean switchToDrawing;
    public ChangeStrokeWidthAction(float w, boolean toDrawing) {
        width = w;
        switchToDrawing = toDrawing;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback baseCallback) {
        final ChangeStrokeWidthRequest changeRequest = new ChangeStrokeWidthRequest(width, switchToDrawing);
        readerDataHolder.getNoteManager().submit(readerDataHolder.getContext(), changeRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                readerDataHolder.getNoteManager().ensureContentRendered();
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }

}
