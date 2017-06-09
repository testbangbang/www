package com.onyx.edu.reader.note.request;

import com.onyx.edu.reader.note.NoteManager;
import com.onyx.edu.reader.note.model.ReaderFormShapeModel;
import com.onyx.edu.reader.note.model.ReaderNoteDataProvider;

import java.util.List;

/**
 * Created by ming on 2017/6/8.
 */

public class lockFormShapesRequest extends ReaderBaseNoteRequest {

    private String documentId;

    public lockFormShapesRequest(String documentId) {
        this.documentId = documentId;
    }

    @Override
    public void execute(NoteManager noteManager) throws Exception {
        super.execute(noteManager);
        List<ReaderFormShapeModel> formShapeModels = ReaderNoteDataProvider.loadFormShapeList(getContext(), documentId, false);
        for (ReaderFormShapeModel formShapeModel : formShapeModels) {
            formShapeModel.setLock(true);
        }
        ReaderNoteDataProvider.saveFormShapeList(getContext(), formShapeModels);
    }
}
