package com.onyx.edu.homework.action.note;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.edu.homework.base.BaseAction;
import com.onyx.edu.homework.base.BaseNoteAction;
import com.onyx.edu.homework.request.GetPageUniqueIdsRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lxm on 2017/12/8.
 */

public class GetPageUniqueIdsAction extends BaseNoteAction {

    private volatile String uniqueId;
    private List<String> pageUniqueIds = new ArrayList<>();

    public GetPageUniqueIdsAction(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Override
    public void execute(NoteViewHelper noteViewHelper, final BaseCallback baseCallback) {
        final GetPageUniqueIdsRequest pageUniqueIdsRequest = new GetPageUniqueIdsRequest(uniqueId);
        noteViewHelper.submit(getAppContext(), pageUniqueIdsRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                pageUniqueIds.addAll(pageUniqueIdsRequest.getPageUniqueIds());
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }

    public List<String> getPageUniqueIds() {
        return pageUniqueIds;
    }
}
