package com.onyx.kreader.note.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.kreader.note.request.ClearPageRequest;
import com.onyx.kreader.note.request.ResumeDrawingRequest;
import com.onyx.kreader.ui.actions.BaseAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.events.RequestFinishEvent;

import java.util.List;

/**
 * Created by zhuzeng on 10/12/16.
 */

public class ResumeDrawingAction extends BaseAction {

    private List<PageInfo> pageList;

    public ResumeDrawingAction(final List<PageInfo> list) {
        pageList = list;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback baseCallback) {
        final ResumeDrawingRequest clearPageRequest = new ResumeDrawingRequest(pageList);
        readerDataHolder.getNoteManager().submit(readerDataHolder.getContext(), clearPageRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }


}
