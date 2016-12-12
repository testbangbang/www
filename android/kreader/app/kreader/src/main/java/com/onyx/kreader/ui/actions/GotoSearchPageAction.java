package com.onyx.kreader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.kreader.api.ReaderSelection;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.request.GotoSearchLocationRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;

import java.util.List;

/**
 * Created by zhuzeng on 5/18/16.
 */
public class GotoSearchPageAction{

    public static void execute(ReaderDataHolder readerDataHolder, String pagePosition, List<ReaderSelection> searchResults, BaseCallback baseCallback){
        BaseReaderRequest gotoPosition = new GotoSearchLocationRequest(pagePosition, readerDataHolder, searchResults);
        readerDataHolder.submitRenderRequest(gotoPosition, baseCallback);
    }

}
