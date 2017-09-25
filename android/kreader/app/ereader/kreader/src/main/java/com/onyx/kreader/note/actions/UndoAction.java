package com.onyx.kreader.note.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.kreader.note.request.UndoRequest;
import com.onyx.kreader.ui.actions.BaseAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.events.ShapeRenderFinishEvent;

/**
 * Created by zhuzeng on 9/23/16.
 */
public class UndoAction extends BaseAction {

    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        PageInfo page = !readerDataHolder.isSideNoting() ?
                readerDataHolder.getVisiblePages().get(0) :
                readerDataHolder.getVisiblePages().get(1);
        final UndoRequest request = new UndoRequest(page);
        readerDataHolder.getNoteManager().submit(readerDataHolder.getContext(), request, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                readerDataHolder.getEventBus().post(ShapeRenderFinishEvent.shapeReadyEventWithUniqueId(Integer.MAX_VALUE));
                BaseCallback.invoke(callback, request, e);
            }
        });
    }

}
