package com.onyx.android.sdk.scribble.request;

import android.content.Context;
import android.util.Pair;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ming on 2017/7/22.
 */

public class NoteRequestChain {

    private boolean abortException = true;

    private List<Pair<BaseNoteRequest, BaseCallback>> pairList = new ArrayList<>();

    public NoteRequestChain addRequest(final BaseNoteRequest request, final BaseCallback callback) {
        addRequestCallBackPair(new Pair<>(request, callback));
        return this;
    }

    public NoteRequestChain addRequestCallBackPair(Pair<BaseNoteRequest, BaseCallback> pair) {
        pairList.add(pair);
        return this;
    }

    public void execute(final Context context, final NoteViewHelper helper) {
        if (isFinished()) {
            return;
        }
        final Pair<BaseNoteRequest, BaseCallback> pair = pairList.remove(0);
        executeRequest(context, helper, pair.first, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                BaseCallback.invoke(pair.second, request, e);
                if (abortException && e != null) {
                    return;
                }
                execute(context, helper);
            }
        });
    }

    private void executeRequest(final Context context, final NoteViewHelper helper, final BaseNoteRequest request, final BaseCallback callback) {
        helper.submit(context, request, callback);
    }

    public boolean isFinished() {
        return CollectionUtils.isNullOrEmpty(pairList);
    }

    public void clearChainList() {
        if (isFinished()) {
            return;
        }
        pairList.clear();
    }

    public boolean isAbortException() {
        return abortException;
    }

    public NoteRequestChain setAbortException(boolean abort) {
        abortException = abort;
        return this;
    }
}
