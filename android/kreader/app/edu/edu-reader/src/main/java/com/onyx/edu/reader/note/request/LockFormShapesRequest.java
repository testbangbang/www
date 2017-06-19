package com.onyx.edu.reader.note.request;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.onyx.edu.reader.note.NoteManager;
import com.onyx.edu.reader.note.model.ReaderFormShapeModel;
import com.onyx.edu.reader.note.model.ReaderNoteDataProvider;

import java.util.List;

/**
 * Created by ming on 2017/6/8.
 */

public class LockFormShapesRequest extends BaseDataRequest {

    private String documentId;

    public LockFormShapesRequest(String documentId) {
        this.documentId = documentId;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        List<ReaderFormShapeModel> formShapeModels = ReaderNoteDataProvider.loadFormShapeList(getContext(), documentId, false);
        for (ReaderFormShapeModel formShapeModel : formShapeModels) {
            formShapeModel.setLock(true);
        }
        ReaderNoteDataProvider.saveFormShapeList(getContext(), formShapeModels);
    }
}
