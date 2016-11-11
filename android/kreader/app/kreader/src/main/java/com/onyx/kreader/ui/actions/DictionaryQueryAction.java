package com.onyx.kreader.ui.actions;

import android.net.Uri;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.kreader.host.request.DictionaryQueryRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by ming on 16/10/14.
 */

public class DictionaryQueryAction extends BaseAction {

    private String token;
    private String expString = "";
    private String dictPath;
    private int state;

    public DictionaryQueryAction(String token) {
        this.token = token;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        final DictionaryQueryRequest resolverQueryRequest = new DictionaryQueryRequest(readerDataHolder,
                token);
        readerDataHolder.submitNonRenderRequest(resolverQueryRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                expString = resolverQueryRequest.getExpString();
                dictPath = resolverQueryRequest.getDictPath();
                state = resolverQueryRequest.getState();
                BaseCallback.invoke(callback, request, e);
            }
        });
    }

    public String getExpString() {
        return expString;
    }

    public String getDictPath() {
        return dictPath;
    }

    public int getState() {
        return state;
    }
}
