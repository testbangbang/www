package com.onyx.jdread.reader.menu.actions;

import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.actions.BaseReaderAction;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.menu.request.GotoSearchLocationRequest;

import java.util.List;

/**
 * Created by huxiaomao on 2018/2/1.
 */

public class GotoSearchPageAction extends BaseReaderAction {
    private String pagePosition;
    private List<ReaderSelection> searchResults;

    public GotoSearchPageAction(String pagePosition, List<ReaderSelection> searchResults) {
        this.pagePosition = pagePosition;
        this.searchResults = searchResults;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, final RxCallback baseCallback) {
        final GotoSearchLocationRequest request = new GotoSearchLocationRequest(searchResults, readerDataHolder.getReader(), pagePosition,readerDataHolder.getSettingInfo());
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                if (baseCallback != null) {
                    baseCallback.onNext(o);
                }
            }
        });
    }
}
