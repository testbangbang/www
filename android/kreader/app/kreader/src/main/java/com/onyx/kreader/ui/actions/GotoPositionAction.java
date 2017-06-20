package com.onyx.kreader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.request.GotoPositionRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.events.PageChangedEvent;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;

/**
 * Created by zhuzeng on 5/18/16.
 */
public class GotoPositionAction extends BaseAction {
    private String pagePosition;
    private boolean abortPendingTasks;

    public GotoPositionAction(final int position) {
        this(PagePositionUtils.fromPosition(position));
    }

    public GotoPositionAction(final String position) {
        this(position, false);
    }

    public GotoPositionAction(final int position, final boolean abortPendingTasks) {
        this(PagePositionUtils.fromPosition(position), abortPendingTasks);
    }

    public GotoPositionAction(final String position, final boolean abortPendingTasks) {
        pagePosition = position;
        this.abortPendingTasks = abortPendingTasks;
    }

    public void execute(final ReaderDataHolder readerDataHolder) {
        final PageChangedEvent pageChangedEvent = readerDataHolder.beforePageChange();
        execute(readerDataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                readerDataHolder.afterPageChange(pageChangedEvent);
            }
        });
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, BaseCallback baseCallback) {
        BaseReaderRequest gotoPosition = new GotoPositionRequest(pagePosition);
        gotoPosition.setAbortPendingTasks(abortPendingTasks);
        readerDataHolder.submitRenderRequest(gotoPosition, baseCallback);
    }
}
