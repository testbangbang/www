package com.onyx.kreader.ui.actions;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.kreader.dataprovider.Bookmark;
import com.onyx.kreader.dataprovider.BookmarkProvider;
import com.onyx.kreader.host.request.AddBookmarkRequest;
import com.onyx.kreader.host.request.DeleteBookmarkRequest;
import com.onyx.kreader.ui.ReaderActivity;

import java.util.Date;

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
    public void execute(ReaderActivity readerActivity) {
        if (toggleSwitch == ToggleSwitch.On) {
            readerActivity.submitRequest(new AddBookmarkRequest(pageInfo));
        } else if (toggleSwitch == ToggleSwitch.Off) {
            Bookmark bookmark = readerActivity.getReaderUserDataInfo().getBookmark(pageInfo);
            readerActivity.submitRequest(new DeleteBookmarkRequest(bookmark));
        }
    }
}
