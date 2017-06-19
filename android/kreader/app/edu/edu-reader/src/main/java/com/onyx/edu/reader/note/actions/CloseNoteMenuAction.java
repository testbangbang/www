package com.onyx.edu.reader.note.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.edu.reader.ui.actions.BaseAction;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;
import com.onyx.edu.reader.ui.events.CloseScribbleMenuEvent;

/**
 * Created by zhuzeng on 9/23/16.
 */
public class CloseNoteMenuAction extends BaseAction {

    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        readerDataHolder.getEventBus().post(new CloseScribbleMenuEvent());
        readerDataHolder.getHandlerManager().resetActiveProvider();
        BaseCallback.invoke(callback, null, null);
    }
}
