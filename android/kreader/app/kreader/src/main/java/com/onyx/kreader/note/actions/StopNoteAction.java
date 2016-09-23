package com.onyx.kreader.note.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.kreader.ui.actions.BaseAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.handler.HandlerManager;

/**
 * Created by zhuzeng on 9/22/16.
 */
public class StopNoteAction extends BaseAction {

    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {

        // hide note toolbar
        readerDataHolder.getHandlerManager().resetToDefaultProvider();
        BaseCallback.invoke(callback, null, null);
    }
}
