package com.onyx.edu.reader.note.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.edu.reader.note.request.RedoRequest;
import com.onyx.edu.reader.ui.actions.BaseAction;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;
import com.onyx.edu.reader.ui.events.ShapeRenderFinishEvent;

/**
 * Created by zhuzeng on 9/23/16.
 */
public class RedoAction extends BaseAction {

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        final RedoRequest request = new RedoRequest(readerDataHolder.getFirstPageInfo());
        readerDataHolder.getNoteManager().submit(readerDataHolder.getContext(), request, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                readerDataHolder.getEventBus().post(ShapeRenderFinishEvent.shapeReadyEvent());
                BaseCallback.invoke(callback, request, e);
            }
        });
    }
}
