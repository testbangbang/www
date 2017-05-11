package com.onyx.edu.reader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;
import com.onyx.edu.reader.ui.events.BeforeDocumentCloseEvent;


/**
 * Created by zhuzeng on 9/22/16.
 */
public class CloseAction extends BaseAction {


    public CloseAction() {
    }

    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        readerDataHolder.getEventBus().post(new BeforeDocumentCloseEvent());
        closeDataHolder(readerDataHolder, callback);
    }

    private void closeDataHolder(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        readerDataHolder.destroy(callback);
    }


}
