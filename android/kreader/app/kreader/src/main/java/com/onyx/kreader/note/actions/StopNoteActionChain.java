package com.onyx.kreader.note.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.kreader.ui.actions.ActionChain;
import com.onyx.kreader.ui.actions.BaseAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.events.CloseScribbleMenuEvent;
import com.onyx.kreader.ui.handler.HandlerManager;

/**
 * Created by zhuzeng on 9/22/16.
 */
public class StopNoteActionChain  {

    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        final ActionChain actionChain = new ActionChain();
        actionChain.addAction(new FlushNoteAction(readerDataHolder.getReaderViewInfo().getVisiblePages(), true, false, false));
        actionChain.addAction(new CloseNoteMenuAction());
        BaseCallback.invoke(callback, null, null);
    }

}
