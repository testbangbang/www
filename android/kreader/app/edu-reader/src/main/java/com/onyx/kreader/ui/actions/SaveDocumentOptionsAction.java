package com.onyx.kreader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.reader.host.request.SaveDocumentOptionsRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by zhuzeng on 17/10/2016.
 */

public class SaveDocumentOptionsAction extends BaseAction {

    public void execute(final ReaderDataHolder readerDataHolder, BaseCallback baseCallback) {
        final SaveDocumentOptionsRequest saveDocumentOptionsRequest = new SaveDocumentOptionsRequest();
        readerDataHolder.submitNonRenderRequest(saveDocumentOptionsRequest, null);
    }
}
