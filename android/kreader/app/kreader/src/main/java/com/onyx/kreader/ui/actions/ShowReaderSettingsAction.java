package com.onyx.kreader.ui.actions;

import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by joy on 8/26/16.
 */
public class ShowReaderSettingsAction extends BaseAction {
    @Override
    public void execute(ReaderDataHolder readerDataHolder) {
        readerDataHolder.showReaderSettings();
    }
}
