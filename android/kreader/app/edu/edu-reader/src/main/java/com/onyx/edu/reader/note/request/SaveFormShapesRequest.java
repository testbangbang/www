package com.onyx.edu.reader.note.request;

import com.onyx.edu.reader.note.NoteManager;
import com.onyx.edu.reader.note.model.ReaderFormShapeModel;
import com.onyx.edu.reader.note.model.ReaderNoteDataProvider;

import java.util.List;

/**
 * Created by lxm on 2017/8/10.
 */

public class SaveFormShapesRequest extends ReaderBaseNoteRequest {

    private List<ReaderFormShapeModel> shapeModels;

    public SaveFormShapesRequest(List<ReaderFormShapeModel> shapeModels) {
        this.shapeModels = shapeModels;
    }

    @Override
    public void execute(NoteManager noteManager) throws Exception {
        if (shapeModels == null || shapeModels.size() == 0) {
            return;
        }
        ReaderNoteDataProvider.saveFormShapeList(getContext(), shapeModels);
        getNoteDataInfo().setContentRendered(true);
    }
}
