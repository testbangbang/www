package com.onyx.kreader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.request.GotoPositionRequest;
import com.onyx.android.sdk.reader.host.request.StartSideNodeRequest;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.events.PageChangedEvent;
import com.onyx.kreader.ui.events.StartSideNoteEvent;

/**
 * Created by zhuzeng on 5/18/16.
 */
public class StartAndGotoSideNotePageAction extends BaseAction {
    final PageInfo pageInfo;

    public StartAndGotoSideNotePageAction(final PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback baseCallback) {
        BaseReaderRequest gotoPosition = new GotoPositionRequest(pageInfo.getPosition());
        readerDataHolder.submitRenderRequest(gotoPosition, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                baseCallback.done(request, e);
                readerDataHolder.setSideNotePageBySubPageIndex(pageInfo.getSubPage());
                readerDataHolder.getEventBus().post(new StartSideNoteEvent());
            }
        });
    }
}
