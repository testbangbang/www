package com.onyx.kreader.ui.actions;

import android.app.Activity;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.kreader.note.actions.FlushNoteAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by zhuzeng on 9/22/16.
 */
public class ReopenDocumentActionChain extends BaseAction {

    private Activity activity;

    public ReopenDocumentActionChain(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        final ActionChain actionChain = new ActionChain();
        if (readerDataHolder.isNoteDirty()) {
            actionChain.addAction(new FlushNoteAction(null, false, false, true, true));
        }
        actionChain.addAction(new CloseAction());
        actionChain.addAction(new OpenDocumentAction(activity, readerDataHolder.getDocumentPath(),readerDataHolder.getBookName(),""));
        actionChain.execute(readerDataHolder, callback);
    }
}
