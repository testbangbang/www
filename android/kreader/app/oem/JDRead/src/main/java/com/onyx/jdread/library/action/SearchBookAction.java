package com.onyx.jdread.library.action;

import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.model.SearchHistory;
import com.onyx.android.sdk.data.utils.QueryBuilder;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.library.model.LibraryDataBundle;
import com.onyx.jdread.library.request.RxSaveSearchHistoryRequest;
import com.onyx.jdread.library.request.RxSearchBookRequest;
import com.onyx.jdread.main.action.BaseAction;
import com.onyx.jdread.util.InputUtils;

/**
 * Created by hehai on 18-1-18.
 */

public class SearchBookAction extends BaseAction<LibraryDataBundle> {
    private boolean submit;

    public SearchBookAction(boolean submit) {
        this.submit = submit;
    }

    @Override
    public void execute(final LibraryDataBundle libraryDataBundle, final RxCallback baseCallback) {
        if (submit) {
            saveSearchHistory(libraryDataBundle);
        }
        String key = libraryDataBundle.getSearchBookModel().searchKey.get();
        key = InputUtils.filterSpecialCharacters(key);
        if (StringUtils.isNullOrEmpty(key)) {
            return;
        }
        QueryArgs queryArgs = QueryBuilder.searchQuery(key, SortBy.None, SortOrder.Asc);
        RxSearchBookRequest request = new RxSearchBookRequest(libraryDataBundle.getDataManager(), queryArgs, libraryDataBundle.getEventBus());
        request.execute(new RxCallback<RxSearchBookRequest>() {
            @Override
            public void onNext(RxSearchBookRequest loadRequest) {
                if (submit) {
                    libraryDataBundle.getSearchBookModel().searchResult.clear();
                    libraryDataBundle.getSearchBookModel().searchResult.addAll(loadRequest.getModels());
                } else {
                    libraryDataBundle.getSearchBookModel().searchHint.clear();
                    libraryDataBundle.getSearchBookModel().searchHint.addAll(loadRequest.getModels());
                }
                baseCallback.onNext(loadRequest);
            }
        });
    }

    private void saveSearchHistory(LibraryDataBundle libraryDataBundle) {
        SearchHistory searchHistory = new SearchHistory();
        searchHistory.setContent(libraryDataBundle.getSearchBookModel().searchKey.get());
        RxSaveSearchHistoryRequest saveSearchHistoryRequest = new RxSaveSearchHistoryRequest(libraryDataBundle.getDataManager(), searchHistory);
        saveSearchHistoryRequest.execute(null);
    }
}
