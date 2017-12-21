package com.onyx.edu.homework.action.note;

import android.widget.Toast;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.navigation.PageRemoveRequest;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.edu.homework.R;
import com.onyx.edu.homework.base.BaseNoteAction;
import com.onyx.edu.homework.event.RequestFinishedEvent;

/**
 * Created by zhuzeng on 6/30/16.
 */
public class DocumentDeletePageAction extends BaseNoteAction {

    @Override
    public void execute(final NoteViewHelper noteViewHelper, final BaseCallback baseCallback) {
        int current = noteViewHelper.getNoteDocument().getCurrentPageIndex();
        int minPageCount = noteViewHelper.getNoteDocument().getMinPageCount();
        if (current < minPageCount) {
            Toast.makeText(getAppContext(), R.string.can_not_delete_page, Toast.LENGTH_SHORT).show();
            BaseCallback.invoke(baseCallback, null, null);
            return;
        }
        final PageRemoveRequest pageRemoveRequest = new PageRemoveRequest();
        noteViewHelper.submit(getAppContext(), pageRemoveRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                post(RequestFinishedEvent.create(pageRemoveRequest, e, true));
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }

}
