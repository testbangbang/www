package com.onyx.jdread.library.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.library.model.LibraryDataBundle;
import com.onyx.jdread.library.request.RxLoadSearchHistoryRequest;
import com.onyx.jdread.main.action.BaseAction;

/**
 * Created by hehai on 18-1-19.
 */

public class LoadSearchHistoryAction extends BaseAction<LibraryDataBundle> {
    private int limit;

    public LoadSearchHistoryAction(int limit) {
        this.limit = limit;
    }

    @Override
    public void execute(final LibraryDataBundle dataBundle, final RxCallback rxCallback) {
        RxLoadSearchHistoryRequest historyRequest = new RxLoadSearchHistoryRequest(dataBundle.getDataManager(), limit);
        historyRequest.execute(new RxCallback<RxLoadSearchHistoryRequest>() {
            @Override
            public void onNext(RxLoadSearchHistoryRequest request) {
                dataBundle.getSearchBookModel().searchHistory.clear();
                dataBundle.getSearchBookModel().searchHistory.addAll(request.getSearchHistoryList());
                rxCallback.onNext(request);
            }
        });
    }
}
