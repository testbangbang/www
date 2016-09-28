package com.onyx.kreader.ui.actions;

import android.widget.RelativeLayout;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.kreader.api.ReaderSearchOptions;
import com.onyx.kreader.ui.ReaderActivity;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.dialog.PopupSearchMenu;
import com.onyx.kreader.utils.PagePositionUtils;


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
    public void execute(ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        getSearchMenu(readerDataHolder).setSearchOptions(searchOptions);
        getSearchMenu(readerDataHolder).show();
        getSearchMenu(readerDataHolder).searchDone(searchResult);
        BaseCallback.invoke(callback, null, null);
    }

    public static void resetSearchMenu() {
        searchMenu = null;
    }

    private PopupSearchMenu getSearchMenu(final ReaderDataHolder readerDataHolder) {
        ReaderActivity readerActivity = (ReaderActivity)readerDataHolder.getContext();
        if (searchMenu == null) {
            searchMenu = new PopupSearchMenu(readerActivity, (RelativeLayout) readerActivity.getSurfaceView().getParent(), new PopupSearchMenu.MenuCallback() {
                @Override
                public void search(PopupSearchMenu.SearchDirection mSearchDirection) {
                    switch (mSearchDirection){
                        case Forward:
                            searchContent(readerDataHolder, readerDataHolder.getCurrentPage() + 1, searchMenu.getSearchOptions().pattern(), true);
                            break;
                        case Backward:
                            searchContent(readerDataHolder, readerDataHolder.getCurrentPage() - 1, searchMenu.getSearchOptions().pattern(), false);
                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void disMissMenu() {
                    searchMenu.hide();
                    searchMenu = null;
                    readerDataHolder.redrawPage();
                }

                @Override
                public void showSearchAll() {

                }
            });
        }
        return searchMenu;
    }

    public void searchContent(final ReaderDataHolder readerDataHolder, int page, String query, boolean forward) {
        searchContent(readerDataHolder, PagePositionUtils.fromPageNumber(page), query, forward);
    }

    private void searchContent(final ReaderDataHolder readerDataHolder, String page, String query, boolean forward) {
//        if (StringUtils.isNotBlank(query)) {
//            new SearchContentAction(page, query, forward).execute(readerDataHolder);
//        }
    }
}
