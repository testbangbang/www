package com.onyx.edu.reader.note.request;

import com.onyx.edu.reader.note.NoteManager;
import com.onyx.edu.reader.note.model.ReaderNoteDataProvider;

import java.util.List;

/**
 * Created by lxm on 2017/8/10.
 */

public class RemoveFormShapesRequest extends ReaderBaseNoteRequest {

    private List<String> shapeIds;

    public RemoveFormShapesRequest(List<String> shapeIds) {
        this.shapeIds = shapeIds;
    }

    @Override
    public void execute(NoteManager noteManager) throws Exception {
        super.execute(noteManager);
        if (shapeIds == null || shapeIds.size() ==0) {
            return;
        }
        ReaderNoteDataProvider.removeFormShapesByIdList(getContext(), shapeIds);
        getNoteDataInfo().setContentRendered(true);
    }
}
