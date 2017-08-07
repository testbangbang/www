package com.onyx.edu.reader.note.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.edu.reader.ui.actions.ActionChain;
import com.onyx.edu.reader.ui.actions.BaseAction;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

/**
 * Created by ming on 2017/8/3.
 */

public class FlushSignatureShapesChain extends BaseAction {

    @Override
    public void execute(ReaderDataHolder readerDataHolder, BaseCallback baseCallback) {
        ActionChain actionChain = new ActionChain();
        actionChain.addAction(new FlushNoteAction(readerDataHolder.getVisiblePages(), false, false, true, false));
        actionChain.addAction(new FlushSignatureShapesAction());
        actionChain.execute(readerDataHolder, baseCallback);
    }
}
