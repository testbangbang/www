package com.onyx.kreader.ui.actions;

import com.onyx.kreader.host.request.ExportNotesRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by zhuzeng on 5/17/16.
 */
public class ExportNotesAction extends BaseAction {

    public void execute(final ReaderDataHolder readerDataHolder) {
        final ExportNotesRequest request = new ExportNotesRequest();
        readerDataHolder.submitNonRenderRequest(request);
    }

}
