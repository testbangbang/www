package com.onyx.kreader.ui.actions;


import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.kreader.host.request.GetTableOfContentRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;

public class GetTableOfContentAction{

    public static void execute(final ReaderDataHolder readerDataHolder,BaseCallback callback) {
        final GetTableOfContentRequest tocRequest = new GetTableOfContentRequest();
        readerDataHolder.getReader().submitRequest(readerDataHolder.getContext(),tocRequest, callback);
    }
}
