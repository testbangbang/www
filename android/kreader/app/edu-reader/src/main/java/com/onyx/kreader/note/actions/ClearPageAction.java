package com.onyx.kreader.note.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.kreader.note.request.ClearPageRequest;
import com.onyx.kreader.ui.actions.BaseAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.events.ShapeRenderFinishEvent;

/**
 * Created by zhuzeng on 9/28/16.
 */

public class ClearPageAction extends BaseAction {

    private PageInfo pageInfo;

    public ClearPageAction(final PageInfo page) {
        pageInfo = page;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback baseCallback) {
        final ClearPageRequest clearPageRequest = new ClearPageRequest(pageInfo);
        readerDataHolder.getNoteManager().submit(readerDataHolder.getContext(), clearPageRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                final ShapeRenderFinishEvent event = ShapeRenderFinishEvent.shapeReadyEventWithFullUpdate();
                readerDataHolder.getEventBus().post(event);
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }

}
