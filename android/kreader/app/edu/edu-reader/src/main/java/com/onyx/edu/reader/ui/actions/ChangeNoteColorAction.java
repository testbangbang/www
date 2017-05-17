package com.onyx.edu.reader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.edu.reader.note.request.ChangeColorRequest;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

/**
 * Created by zhuzeng on 26/11/2016.
 */

public class ChangeNoteColorAction extends BaseAction {

    private int color;
    public ChangeNoteColorAction(int c) {
        color = c;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback baseCallback) {
        final ChangeColorRequest changeColorRequest = new ChangeColorRequest(color);
        readerDataHolder.getNoteManager().submit(readerDataHolder.getContext(), changeColorRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                readerDataHolder.getNoteManager().ensureContentRendered();
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }
}
