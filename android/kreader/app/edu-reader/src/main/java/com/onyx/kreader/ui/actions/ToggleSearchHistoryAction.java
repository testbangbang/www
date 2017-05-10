package com.onyx.kreader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.reader.host.request.AddSearchHistoryRequest;
import com.onyx.android.sdk.reader.host.request.DeleteSearchHistoryRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by ming on 16/8/8.
 */
public class ToggleSearchHistoryAction extends BaseAction {

    private String content;
    private boolean add;

    public ToggleSearchHistoryAction(String content, boolean add) {
        this.content = content;
        this.add = add;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        if (add) {
            readerDataHolder.submitNonRenderRequest(new AddSearchHistoryRequest(content), callback);
        } else {
            readerDataHolder.submitNonRenderRequest(new DeleteSearchHistoryRequest(), callback);
        }
    }
}
