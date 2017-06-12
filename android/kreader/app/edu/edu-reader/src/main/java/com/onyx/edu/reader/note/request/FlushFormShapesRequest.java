package com.onyx.edu.reader.note.request;

import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.edu.reader.note.NoteManager;
import com.onyx.edu.reader.note.data.ReaderShapeFactory;
import com.onyx.edu.reader.note.model.ReaderFormShapeModel;
import com.onyx.edu.reader.note.model.ReaderNoteDataProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ming on 2017/6/6.
 */

public class FlushFormShapesRequest extends ReaderBaseNoteRequest {

    private List<Shape> shapes;

    public FlushFormShapesRequest(List<Shape> shapes) {
        this.shapes = shapes;
    }

    @Override
    public void execute(NoteManager noteManager) throws Exception {
        if (shapes == null) {
            return;
        }
        List<ReaderFormShapeModel> formShapes = new ArrayList<>();
        for (Shape shape : shapes) {
            ReaderFormShapeModel mode = (ReaderFormShapeModel) ReaderShapeFactory.modelFromShape(shape);
            formShapes.add(mode);
            if (ReaderShapeFactory.isUniqueFormShape(shape.getFormType())) {
                ReaderNoteDataProvider.removeFormShape(getContext(), shape.getDocumentUniqueId(), shape.getFormId());
            }
        }
        ReaderNoteDataProvider.saveFormShapeList(getContext(), formShapes);

    }
}
