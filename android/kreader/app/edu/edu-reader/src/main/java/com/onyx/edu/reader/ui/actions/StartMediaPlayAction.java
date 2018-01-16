package com.onyx.edu.reader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;
import com.onyx.edu.reader.ui.handler.HandlerManager;

/**
 * Created by lxm on 2018/1/12.
 */

public class StartMediaPlayAction extends BaseAction {

    @Override
    public void execute(ReaderDataHolder readerDataHolder, BaseCallback baseCallback) {
        readerDataHolder.getHandlerManager().setActiveProvider(HandlerManager.MEDIA_PLAY_PROVIDER);
        BaseCallback.invoke(baseCallback, null, null);
    }
}
