package com.onyx.kreader.note.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.kreader.note.request.ChangeShapeRequest;
import com.onyx.kreader.note.request.ChangeStrokeWidthRequest;
import com.onyx.kreader.ui.actions.BaseAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by zhuzeng on 9/23/16.
 */
public class ChangeStrokeWidthAction extends BaseAction {

    private float width;
    public ChangeStrokeWidthAction(float w) {
        width = w;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, BaseCallback baseCallback) {
        final ChangeStrokeWidthRequest changeRequest = new ChangeStrokeWidthRequest(width);
        readerDataHolder.getNoteManager().submit(readerDataHolder.getContext(), changeRequest, baseCallback);
    }

}
