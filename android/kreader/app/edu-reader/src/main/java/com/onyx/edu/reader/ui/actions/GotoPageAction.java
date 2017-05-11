package com.onyx.edu.reader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.reader.host.request.GotoPageRequest;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;
import com.onyx.edu.reader.ui.events.PageChangedEvent;

/**
 * Created by zhuzeng on 5/18/16.
 */
public class GotoPageAction extends BaseAction {
    private int page;
    private boolean abortPendingTasks;

    public GotoPageAction(final int page) {
        this(page, false);
    }

    public GotoPageAction(final int page, final boolean abortPendingTasks) {
        this.page = page;
        this.abortPendingTasks = abortPendingTasks;
    }

    public void execute(final ReaderDataHolder readerDataHolder) {
        final PageChangedEvent pageChangedEvent = PageChangedEvent.beforePageChange(readerDataHolder);
        execute(readerDataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                pageChangedEvent.afterPageChange(readerDataHolder);
            }
        });
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, BaseCallback baseCallback) {
        GotoPageRequest request = new GotoPageRequest(page);
        request.setAbortPendingTasks(abortPendingTasks);
        readerDataHolder.submitRenderRequest(request, baseCallback);
    }
}
