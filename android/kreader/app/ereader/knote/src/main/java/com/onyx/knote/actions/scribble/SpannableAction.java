package com.onyx.knote.actions.scribble;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.asyncrequest.shape.SpannableRequest;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.knote.actions.BaseNoteAction;

import java.util.List;
import java.util.Map;

/**
 * Created by solskjaer49 on 2017/6/30 15:57.
 */

public class SpannableAction extends BaseNoteAction {
    public SpannableAction(Map<String, List<Shape>> subPageSpanTextShapeMap, List<Shape> newAddShapeList) {
        this.subPageSpanTextShapeMap = subPageSpanTextShapeMap;
        this.newAddShapeList = newAddShapeList;
    }

    private Map<String, List<Shape>> subPageSpanTextShapeMap;
    private List<Shape> newAddShapeList;

    @Override
    public void execute(NoteManager noteManager, BaseCallback callback) {
        SpannableRequest spannableRequest = new SpannableRequest(subPageSpanTextShapeMap, newAddShapeList);
        noteManager.submitRequest(spannableRequest, callback);
    }
}