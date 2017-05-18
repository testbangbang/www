package com.onyx.kreader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by joy on 8/26/16.
 */
public class ShowReaderSettingsAction extends BaseAction {
    @Override
    public void execute(ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        readerDataHolder.showReaderSettings();
        BaseCallback.invoke(callback, null, null);
    }
}
