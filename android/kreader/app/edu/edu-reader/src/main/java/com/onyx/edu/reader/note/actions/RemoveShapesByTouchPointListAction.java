package com.onyx.edu.reader.note.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.edu.reader.note.request.RemoveShapesByTouchPointListRequest;
import com.onyx.edu.reader.ui.actions.BaseAction;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;
import com.onyx.edu.reader.ui.events.ShapeRenderFinishEvent;

import java.util.List;

/**
 * Created by zhuzeng on 9/30/16.
 */

public class RemoveShapesByTouchPointListAction extends BaseAction {

    private List<PageInfo> visiblePages;
    private TouchPointList touchPointList;

    public RemoveShapesByTouchPointListAction(final List<PageInfo> pageList, final TouchPointList list) {
        visiblePages = pageList;
        touchPointList = list;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback baseCallback) {
        final RemoveShapesByTouchPointListRequest removeRequest = new RemoveShapesByTouchPointListRequest(visiblePages, touchPointList);
        readerDataHolder.getNoteManager().submit(readerDataHolder.getContext(), removeRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                readerDataHolder.getHandlerManager().getActiveProvider().onShapesRemoved(removeRequest.getRemovedShapeList());
                readerDataHolder.getEventBus().post(ShapeRenderFinishEvent.shapeReadyEvent());
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }
}
