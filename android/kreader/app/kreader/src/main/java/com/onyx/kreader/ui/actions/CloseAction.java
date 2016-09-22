package com.onyx.kreader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.events.BeforeDocumentClose;


/**
 * Created by zhuzeng on 9/22/16.
 */
public class CloseAction extends BaseAction {


    public CloseAction() {
    }

    public void execute(final ReaderDataHolder readerDataHolder) {
    }

    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        readerDataHolder.getEventBus().post(new BeforeDocumentClose());
        closeDataHolder(readerDataHolder);
        BaseCallback.invoke(callback, null, null);
    }

    private void closeDataHolder(final ReaderDataHolder readerDataHolder) {
        readerDataHolder.getEventBus().unregister(this);
        readerDataHolder.destroy();
    }


}
