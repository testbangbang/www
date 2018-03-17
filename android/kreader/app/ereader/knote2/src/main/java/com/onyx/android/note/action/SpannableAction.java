package com.onyx.android.note.action;

import android.support.annotation.NonNull;

import com.onyx.android.note.common.base.BaseNoteAction;
import com.onyx.android.note.event.SpannableEvent;
import com.onyx.android.sdk.note.NoteManager;
import com.onyx.android.sdk.note.request.SpannableRequest;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.scribble.shape.Shape;

import java.util.List;
import java.util.Map;

/**
 * Created by lxm on 2018/3/16.
 */

public class SpannableAction extends BaseNoteAction {

    private Map<String, List<Shape>> pageSpanTextShapeMap;
    private List<Shape> newAddShapes;

    public SpannableAction(NoteManager noteManager, Map<String, List<Shape>> pageSpanTextShapeMap, List<Shape> newAddShapes) {
        super(noteManager);
        this.pageSpanTextShapeMap = pageSpanTextShapeMap;
        this.newAddShapes = newAddShapes;
    }

    @Override
    public void execute(final RxCallback rxCallback) {
        SpannableRequest request = new SpannableRequest(getNoteManager(), pageSpanTextShapeMap, newAddShapes);
        request.setRenderToScreen(false);
        getNoteManager().getRxManager().enqueue(request, new RxCallback<SpannableRequest>() {
            @Override
            public void onNext(@NonNull SpannableRequest request) {
                RxCallback.onNext(rxCallback, request);
                getNoteManager().post(new SpannableEvent(true));
            }
        });
    }
}
