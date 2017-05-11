package com.onyx.kreader.note.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.kreader.ui.actions.BaseAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.events.CloseScribbleMenuEvent;

/**
 * Created by zhuzeng on 9/23/16.
 */
public class CloseNoteMenuAction extends BaseAction {

    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        readerDataHolder.getEventBus().post(new CloseScribbleMenuEvent());
        readerDataHolder.getHandlerManager().resetToDefaultProvider();
        BaseCallback.invoke(callback, null, null);
    }
}
