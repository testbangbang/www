package com.onyx.edu.reader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.edu.reader.ui.requests.DictionaryQueryRequest;
import com.onyx.edu.reader.ui.data.DictionaryQuery;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

import java.util.ArrayList;
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

    public List<DictionaryQuery> getAllDictionaryQueries() {
        return dictionaryQueries;
    }

    public List<DictionaryQuery> getSoundDictionaryQueries() {
        List<DictionaryQuery> SoundDictionaryQueries = new ArrayList<>();
        if (dictionaryQueries != null) {
            for (DictionaryQuery dictionaryQuery : dictionaryQueries) {
                String soundPath = dictionaryQuery.getSoundPath();
                if (!StringUtils.isNullOrEmpty(soundPath)) {
                    SoundDictionaryQueries.add(dictionaryQuery);
                }
            }
        }
        return SoundDictionaryQueries;
    }

    public List<DictionaryQuery> getTextDictionaryQueries() {
        List<DictionaryQuery> SoundDictionaryQueries = new ArrayList<>();
        if (dictionaryQueries != null) {
            for (DictionaryQuery dictionaryQuery : dictionaryQueries) {
                String soundPath = dictionaryQuery.getSoundPath();
                if (StringUtils.isNullOrEmpty(soundPath)) {
                    SoundDictionaryQueries.add(dictionaryQuery);
                }
            }
        }
        return SoundDictionaryQueries;
    }

    public String getErrorInfo() {
        return errorInfo;
    }
}
