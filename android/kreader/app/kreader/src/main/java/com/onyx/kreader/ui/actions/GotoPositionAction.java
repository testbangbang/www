package com.onyx.kreader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.request.GotoPositionRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by zhuzeng on 5/18/16.
 */
public class GotoPositionAction extends BaseAction {
    private String pageName;
    private boolean abortPendingTasks;

    public GotoPositionAction(final String name) {
        this(name, false);
    }

    public GotoPositionAction(final String name, final boolean abortPendingTasks) {
        pageName = name;
        this.abortPendingTasks = abortPendingTasks;
    }

    public void execute(final ReaderDataHolder readerDataHolder) {
        execute(readerDataHolder, null);
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, BaseCallback baseCallback) {
        BaseReaderRequest gotoPosition = new GotoPositionRequest(pageName);
        gotoPosition.setAbortPendingTasks(abortPendingTasks);
        readerDataHolder.submitRenderRequest(gotoPosition, baseCallback);
    }
}
