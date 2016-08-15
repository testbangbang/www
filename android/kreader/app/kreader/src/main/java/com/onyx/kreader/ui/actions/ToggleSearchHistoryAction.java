package com.onyx.kreader.ui.actions;

import com.onyx.kreader.host.request.AddSearchHistoryRequest;
import com.onyx.kreader.host.request.DeleteSearchHistoryRequest;
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
    public void execute(ReaderDataHolder readerDataHolder) {
        if (add) {
            readerDataHolder.submitNonRenderRequest(new AddSearchHistoryRequest(content));
        } else {
            readerDataHolder.submitNonRenderRequest(new DeleteSearchHistoryRequest());
        }
    }
}
