package com.onyx.kreader.ui.actions;

import com.onyx.kreader.host.request.AddSearchHistoryRequest;
import com.onyx.kreader.host.request.DeleteSearchHistoryRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by ming on 16/8/8.
 */
public class ToggleSearchHistoryAction extends BaseAction {

    public enum SearchToggleSwitch { On, Off }

    private String content;
    private SearchToggleSwitch searchToggleSwitch;

    public ToggleSearchHistoryAction(String content, SearchToggleSwitch searchToggleSwitch) {
        this.content = content;
        this.searchToggleSwitch = searchToggleSwitch;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder) {
        if (searchToggleSwitch == SearchToggleSwitch.On) {
            readerDataHolder.submitRenderRequest(new AddSearchHistoryRequest(content));
        } else if (searchToggleSwitch == SearchToggleSwitch.Off) {
            readerDataHolder.submitRenderRequest(new DeleteSearchHistoryRequest());
        }
    }
}
