package com.onyx.kreader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.reader.host.request.CheckExaminataionRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by li on 2017/9/21.
 */

public class CheckExaminationDataAction extends BaseAction {
    private String bookId;

    public CheckExaminationDataAction(String bookId) {
        this.bookId = bookId;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, BaseCallback baseCallback) {
        readerDataHolder.submitRequest(new CheckExaminataionRequest(bookId), baseCallback);
    }
}
