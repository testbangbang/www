package com.onyx.android.note.action;

import android.support.annotation.NonNull;

import com.onyx.android.note.common.base.BaseNoteAction;
import com.onyx.android.note.event.PageSpanShapesEvent;
import com.onyx.android.sdk.note.NoteManager;
import com.onyx.android.sdk.note.request.PageSpanShapesRequest;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.scribble.shape.Shape;

import java.util.List;
import java.util.Map;

/**
 * Created by lxm on 2018/3/16.
 */

public class PageSpanShapesAction extends BaseNoteAction {

    private String pageUniqueName;

    public PageSpanShapesAction(NoteManager noteManager, String pageUniqueName) {
        super(noteManager);
        this.pageUniqueName = pageUniqueName;
    }

    @Override
    public void execute(final RxCallback rxCallback) {
        PageSpanShapesRequest request = new PageSpanShapesRequest(getNoteManager(), pageUniqueName);
        getNoteManager().getRxManager().enqueue(request, new RxCallback<PageSpanShapesRequest>() {
            @Override
            public void onNext(@NonNull PageSpanShapesRequest request) {
                RxCallback.onNext(rxCallback, request);
                getNoteManager().post(new PageSpanShapesEvent(true));
            }
        });
    }
}
