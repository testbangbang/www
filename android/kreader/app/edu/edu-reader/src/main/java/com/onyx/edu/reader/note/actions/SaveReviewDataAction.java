package com.onyx.edu.reader.note.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.edu.reader.note.data.ReaderNotePageNameMap;
import com.onyx.edu.reader.note.model.ReaderFormShapeModel;
import com.onyx.edu.reader.note.request.SaveReviewDataRequest;
import com.onyx.edu.reader.ui.actions.BaseAction;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

import java.util.List;

/**
 * Created by ming on 2017/6/12.
 */

public class SaveReviewDataAction extends BaseAction {

    private ReaderNotePageNameMap pageNameMap;
    private List<ReaderFormShapeModel> formShapeModels;
    private String documentUniqueId;

    public SaveReviewDataAction(ReaderNotePageNameMap pageNameMap, List<ReaderFormShapeModel> formShapeModels, String documentUniqueId) {
        this.pageNameMap = pageNameMap;
        this.formShapeModels = formShapeModels;
        this.documentUniqueId = documentUniqueId;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, BaseCallback baseCallback) {
        SaveReviewDataRequest saveReviewDataRequest = new SaveReviewDataRequest(pageNameMap, formShapeModels, documentUniqueId);
        readerDataHolder.getNoteManager().submit(readerDataHolder.getContext(), saveReviewDataRequest, baseCallback);
    }
}
