package com.onyx.kreader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.data.model.Bookmark;
import com.onyx.android.sdk.reader.host.request.AddBookmarkRequest;
import com.onyx.android.sdk.reader.host.request.DeleteBookmarkRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by joy on 7/7/16.
 */
public class ToggleBookmarkAction extends BaseAction {

    public enum ToggleSwitch { On, Off }

    private PageInfo pageInfo;
    private ToggleSwitch toggleSwitch;

    public ToggleBookmarkAction(PageInfo pageInfo, ToggleSwitch toggleSwitch) {
        this.pageInfo = pageInfo;
        this.toggleSwitch = toggleSwitch;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        if (toggleSwitch == ToggleSwitch.On) {
            readerDataHolder.submitRenderRequest(new AddBookmarkRequest(pageInfo), callback);
        } else if (toggleSwitch == ToggleSwitch.Off) {
            Bookmark bookmark = readerDataHolder.getReaderUserDataInfo().getBookmark(pageInfo);
            readerDataHolder.submitRenderRequest(new DeleteBookmarkRequest(bookmark), callback);
        }
    }
}
