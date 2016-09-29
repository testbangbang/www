package com.onyx.kreader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.kreader.host.request.ExportNotesRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by zhuzeng on 5/17/16.
 */
public class ExportNotesAction extends BaseAction {

    @Override
    public void execute(ReaderDataHolder readerDataHolder, BaseCallback baseCallback) {
        final ExportNotesRequest request = new ExportNotesRequest(readerDataHolder.getNoteManager());
        readerDataHolder.submitNonRenderRequest(request, baseCallback);
    }
}
