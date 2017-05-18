package com.onyx.edu.reader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;
import com.onyx.edu.reader.ui.handler.HandlerManager;
import com.onyx.edu.reader.ui.handler.TtsHandler;

/**
 * Created by joy on 8/24/16.
 */
public class StartTtsAction extends BaseAction {

    private String startPosition;

    public StartTtsAction(final String startPosition) {
        this.startPosition = startPosition;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        readerDataHolder.getHandlerManager().setActiveProvider(HandlerManager.TTS_PROVIDER,
                TtsHandler.createInitialState(startPosition));
        BaseCallback.invoke(callback, null, null);
    }
}
