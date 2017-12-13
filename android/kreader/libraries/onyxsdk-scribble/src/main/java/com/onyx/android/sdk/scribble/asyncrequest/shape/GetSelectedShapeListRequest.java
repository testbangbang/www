package com.onyx.android.sdk.scribble.asyncrequest.shape;

import android.graphics.RectF;

import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.data.SelectedRectF;
import com.onyx.android.sdk.scribble.shape.Shape;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by solskjaer49 on 2017/8/9 16:25.
 */

public class GetSelectedShapeListRequest extends AsyncBaseNoteRequest {
    public List<Shape> getSelectedShapeList() {
        return selectedShapeList;
    }

    private volatile List<Shape> selectedShapeList = new ArrayList<>();

    public SelectedRectF getSelectedRectF() {
        return selectedRectF;
    }

    private volatile SelectedRectF selectedRectF = new SelectedRectF(new RectF());

    public GetSelectedShapeListRequest() {
        setPauseInputProcessor(true);
    }

    @Override
    public void execute(final NoteManager parent) throws Exception {
        selectedShapeList = parent.getNoteDocument().getCurrentPage(getContext()).getSelectedShapeList();
        selectedRectF = parent.getNoteDocument().getCurrentPage(getContext()).getSelectedRect();
    }

}
