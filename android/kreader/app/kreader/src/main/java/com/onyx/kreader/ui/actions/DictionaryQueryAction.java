package com.onyx.kreader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.kreader.ui.requests.DictionaryQueryRequest;
import com.onyx.kreader.ui.data.DictionaryQuery;
import com.onyx.kreader.ui.data.ReaderDataHolder;

import java.util.List;

/**
 * Created by ming on 16/10/14.
 */

public class DictionaryQueryAction extends BaseAction {

    private String token;
    private List<DictionaryQuery> dictionaryQueries;
    private String errorInfo;


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
                errorInfo = resolverQueryRequest.getErrorInfo();
                dictionaryQueries = resolverQueryRequest.getDictionaryQueries();
                BaseCallback.invoke(callback, request, e);
            }
        });
    }

    public List<DictionaryQuery> getDictionaryQueries() {
        return dictionaryQueries;
    }

    public String getErrorInfo() {
        return errorInfo;
    }
}
