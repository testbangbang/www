package com.onyx.kreader.note.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.kreader.host.request.SaveDocumentOptionsRequest;
import com.onyx.kreader.ui.actions.ActionChain;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.handler.HandlerManager;

/**
 * Created by zhuzeng on 9/22/16.
 */
public class StopNoteActionChain  {

    private boolean render;
    private boolean transfer;
    private boolean save;
    private boolean show;

    public StopNoteActionChain(boolean render, boolean transfer, boolean saveToDatabase, boolean showDialog) {
        this.render = render;
        this.transfer = transfer;
        save = saveToDatabase;
        show = showDialog;
    }

    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        final ActionChain actionChain = new ActionChain();
        actionChain.addAction(new FlushNoteAction(readerDataHolder.getVisiblePages(), render, transfer, save, show));
        actionChain.addAction(new CloseNoteMenuAction());
        actionChain.execute(readerDataHolder, callback);
    }

}
