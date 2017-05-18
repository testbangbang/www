package com.onyx.edu.reader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.reader.host.request.GotoPageRequest;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

/**
 * Created by zhuzeng on 5/18/16.
 */
public class RefreshCurrentPageAction extends BaseAction {
    private boolean abortPendingTasks;

    public RefreshCurrentPageAction() {
        this(false);
    }

    public RefreshCurrentPageAction(final boolean abortPendingTasks) {
        this.abortPendingTasks = abortPendingTasks;
    }

    public void execute(final ReaderDataHolder readerDataHolder) {
        execute(readerDataHolder, null);
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, BaseCallback baseCallback) {
        GotoPageRequest request = new GotoPageRequest(readerDataHolder.getCurrentPage());
        request.setAbortPendingTasks(abortPendingTasks);
        readerDataHolder.submitRenderRequest(request, baseCallback);
    }
}
