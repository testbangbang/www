package com.onyx.edu.reader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.edu.reader.note.actions.FlushNoteAction;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

/**
 * Created by zhuzeng on 9/22/16.
 */
public class CloseActionChain {

    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        final ActionChain actionChain = new ActionChain();
        if (readerDataHolder.isNoteDirty()) {
            actionChain.addAction(new FlushNoteAction(null, false, false, true, true));
        }
        actionChain.addAction(new CloseAction());
        actionChain.execute(readerDataHolder, callback);
    }
}
