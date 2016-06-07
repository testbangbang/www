package com.onyx.kreader.ui.actions;

import android.widget.RelativeLayout;
import com.onyx.kreader.api.ReaderSearchOptions;
import com.onyx.kreader.ui.ReaderActivity;
import com.onyx.kreader.ui.dialog.PopupSearchMenu;
import com.onyx.kreader.utils.PagePositionUtils;
import com.onyx.kreader.utils.StringUtils;

/**
 * Created by Joy on 2016/6/7.
 */
public class ShowSearchMenuAction extends BaseAction {

    // when in search, we will always re-use the menu, so make to be static
    private static PopupSearchMenu searchMenu;

    private ReaderSearchOptions searchOptions;
    private PopupSearchMenu.SearchResult searchResult;

    public ShowSearchMenuAction(final ReaderSearchOptions options, final PopupSearchMenu.SearchResult result) {
        searchOptions = options;
        searchResult = result;
    }

    @Override
    public void execute(ReaderActivity readerActivity) {
        getSearchMenu(readerActivity).setSearchOptions(searchOptions);
        getSearchMenu(readerActivity).show();
        getSearchMenu(readerActivity).searchDone(searchResult);
    }

    private PopupSearchMenu getSearchMenu(final ReaderActivity readerActivity) {
        if (searchMenu == null) {
            searchMenu = new PopupSearchMenu(readerActivity, (RelativeLayout)readerActivity.getSurfaceView().getParent(), new PopupSearchMenu.MenuCallback() {
                @Override
                public void search(PopupSearchMenu.SearchDirection mSearchDirection) {
                    switch (mSearchDirection){
                        case Forward:
                            searchContent(readerActivity, readerActivity.getCurrentPage() + 1, searchMenu.getSearchOptions().pattern(), true);
                            break;
                        case Backward:
                            searchContent(readerActivity, readerActivity.getCurrentPage() - 1, searchMenu.getSearchOptions().pattern(), false);
                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void disMissMenu() {
                    searchMenu.hide();
                    searchMenu = null;
                    readerActivity.redrawPage();
                }

                @Override
                public void showSearchAll() {

                }
            });
        }
        return searchMenu;
    }

    public void searchContent(final ReaderActivity readerActivity, int page, String query, boolean forward) {
        searchContent(readerActivity, PagePositionUtils.fromPageNumber(page), query, forward);
    }

    private void searchContent(final ReaderActivity readerActivity, String page, String query, boolean forward) {
        if (StringUtils.isNotBlank(query)) {
            new SearchContentAction(page, query, forward).execute(readerActivity);
        }
    }
}
