package com.onyx.kreader.action;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.reader.host.request.NextScreenRequest;
import com.onyx.kreader.reader.data.ReaderDataHolder;

/**
 * Created by ming on 2017/4/28.
 */

public class NextScreenAction extends BaseAction {

    @Override
    public void execute(ReaderDataHolder readerDataHolder, BaseCallback baseCallback) {
        NextScreenRequest nextScreenRequest = new NextScreenRequest();
        readerDataHolder.submitRenderRequest(nextScreenRequest, baseCallback);
    }
}
