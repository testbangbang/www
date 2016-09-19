package com.onyx.kreader.ui.actions;

import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.dialog.DialogTts;
import com.onyx.kreader.ui.handler.HandlerManager;

/**
 * Created by joy on 8/24/16.
 */
public class StartTtsAction extends BaseAction {
    private DialogTts dialog;

    @Override
    public void execute(ReaderDataHolder readerDataHolder) {
        readerDataHolder.getHandlerManager().setActiveProvider(HandlerManager.TTS_PROVIDER);
        dialog = new DialogTts(readerDataHolder);
        dialog.show();
    }

    public DialogTts getDialog() {
        return dialog;
    }
}
