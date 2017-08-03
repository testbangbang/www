package com.onyx.android.sdk.scribble.asyncrequest.note;

import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncNoteViewHelper;
import com.onyx.android.sdk.scribble.shape.Shape;

import java.util.List;

/**
 * Created by ming on 2016/12/15.
 */

public class NotePageShapesRequest extends AsyncBaseNoteRequest {

    private  List<Shape> pageShapes;
    private String pageUniqueName;

    public NotePageShapesRequest(String pageUniqueName) {
        this.pageUniqueName = pageUniqueName;
    }

    @Override
    public void execute(AsyncNoteViewHelper helper) throws Exception {
        pageShapes =  helper.getNoteDocument().getNotePage(getContext(), pageUniqueName).getShapeList();
    }

    public List<Shape> getPageShapes() {
        return pageShapes;
    }
}
