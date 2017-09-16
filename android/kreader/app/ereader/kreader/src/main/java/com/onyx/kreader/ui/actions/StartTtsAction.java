package com.onyx.kreader.ui.actions;

import android.app.Dialog;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.dialog.DialogTts;
import com.onyx.kreader.ui.handler.HandlerManager;
import com.onyx.kreader.ui.handler.TtsHandler;

/**
 * Created by joy on 8/24/16.
 */
public class StartTtsAction extends BaseAction {

    private String startPosition;
    private String audioPath;

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    public StartTtsAction(final String startPosition) {
        this.startPosition = startPosition;
        audioPath = null;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        readerDataHolder.getHandlerManager().setActiveProvider(HandlerManager.TTS_PROVIDER,
                TtsHandler.createInitialState(startPosition,audioPath));
        BaseCallback.invoke(callback, null, null);
    }
}
