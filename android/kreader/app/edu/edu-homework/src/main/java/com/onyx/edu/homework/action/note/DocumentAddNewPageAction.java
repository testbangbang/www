package com.onyx.edu.homework.action.note;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.navigation.PageAddRequest;
import com.onyx.edu.homework.base.BaseNoteAction;
import com.onyx.edu.homework.event.RequestFinishedEvent;

/**
 * Created by zhuzeng on 6/30/16.
 */
public class DocumentAddNewPageAction extends BaseNoteAction {

    private int position;

    public DocumentAddNewPageAction(int pos) {
        position = pos;
    }

    @Override
    public void execute(final NoteViewHelper noteViewHelper, final BaseCallback baseCallback) {
        final PageAddRequest pageAddRequest = new PageAddRequest(position);
        noteViewHelper.submit(getAppContext(), pageAddRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                post(RequestFinishedEvent.create(pageAddRequest, e, true));
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }
}
