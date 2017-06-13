package com.onyx.edu.reader.note.request;

import com.onyx.edu.reader.note.NoteManager;
import com.onyx.edu.reader.note.data.ReaderNotePageNameMap;
import com.onyx.edu.reader.note.model.ReaderFormShapeModel;
import com.onyx.edu.reader.note.model.ReaderNoteDataProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ming on 2017/6/12.
 */

public class SaveReviewDataRequest extends ReaderBaseNoteRequest {

    private ReaderNotePageNameMap pageNameMap;
    private List<ReaderFormShapeModel> formShapeModels;
    private String documentUniqueId;

    public SaveReviewDataRequest(ReaderNotePageNameMap pageNameMap, List<ReaderFormShapeModel> formShapeModels, String documentUniqueId) {
        this.pageNameMap = pageNameMap;
        this.formShapeModels = formShapeModels;
        this.documentUniqueId = documentUniqueId;
    }

    @Override
    public void execute(NoteManager noteManager) throws Exception {
        ensureDocumentOpened(noteManager);
        List<ReaderFormShapeModel> newFormShapeModels = new ArrayList<>();
        for (ReaderFormShapeModel formShapeModel : formShapeModels) {
            if (!ReaderNoteDataProvider.hasFormShape(getContext(), formShapeModel.getShapeUniqueId())) {
                formShapeModel.setDocumentUniqueId(documentUniqueId);
                newFormShapeModels.add(formShapeModel);
            }
        }
        ReaderNoteDataProvider.saveFormShapeList(getContext(), newFormShapeModels);
        noteManager.getNoteDocument().addPageIndex(pageNameMap);
        getNoteDataInfo().setContentRendered(renderVisiblePages(noteManager));
    }
}
