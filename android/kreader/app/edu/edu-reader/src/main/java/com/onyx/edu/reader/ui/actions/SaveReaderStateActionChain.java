package com.onyx.edu.reader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.edu.reader.note.actions.FlushNoteAction;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

/**
 * Created by ming on 2017/8/4.
 */

public class SaveReaderStateActionChain extends BaseAction {

    @Override
    public void execute(ReaderDataHolder readerDataHolder, BaseCallback baseCallback) {
        ActionChain actionChain = new ActionChain();
        actionChain.addAction(new SaveDocumentOptionsAction());
        actionChain.addAction(new FlushNoteAction(readerDataHolder.getVisiblePages(), false, false, true, false));
        actionChain.execute(readerDataHolder, baseCallback);
    }

}
