package com.onyx.android.sdk.reader.host.request;

import android.util.Pair;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.wrapper.Reader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 10/4/16.
 */

public class ReaderRequestChain {

    private List<Pair<BaseReaderRequest, BaseCallback>> pairList = new ArrayList<>();

    public ReaderRequestChain addRequest(final BaseReaderRequest request, final BaseCallback callback) {
        pairList.add(new Pair(request, callback));
        return this;
    }

    public void execute(final Reader reader) {
        if (isFinished()) {
            return;
        }

        final Pair<BaseReaderRequest, BaseCallback> pair = pairList.remove(0);
        executeRequest(reader, pair.first, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                BaseCallback.invoke(pair.second, request, e);
                execute(reader);
            }
        });
    }

    private void executeRequest(final Reader reader, final BaseReaderRequest request, final BaseCallback callback) {
        reader.submitRequest(request.getContext(), request, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                BaseCallback.invoke(callback, request, e);
            }
        });
    }

    private boolean isFinished() {
        if (pairList.size() <= 0) {
            return true;
        }
        return false;
    }


}
