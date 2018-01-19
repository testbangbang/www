package com.onyx.jdread.library.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.library.model.LibraryDataBundle;
import com.onyx.jdread.library.request.RxClearSearchHistory;
import com.onyx.jdread.main.action.BaseAction;

/**
 * Created by hehai on 18-1-19.
 */

public class ClearSearchHistoryAction extends BaseAction<LibraryDataBundle> {
    @Override
    public void execute(final LibraryDataBundle dataBundle, RxCallback rxCallback) {
        RxClearSearchHistory clearSearchHistory = new RxClearSearchHistory(dataBundle.getDataManager());
        clearSearchHistory.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                dataBundle.getSearchBookModel().searchHistory.clear();
            }
        });
    }
}
