package com.onyx.edu.reader.note.request;

import android.graphics.Color;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.reader.api.ReaderException;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.edu.reader.note.NoteManager;
import com.onyx.edu.reader.note.data.ReaderNotePageNameMap;
import com.onyx.edu.reader.note.model.ReaderFormShapeModel;
import com.onyx.edu.reader.note.model.ReaderNoteDataProvider;
import com.onyx.edu.reader.ui.data.ReviewDocumentData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ming on 2017/6/12.
 */

public class SaveReviewDataRequest extends ReaderBaseNoteRequest {

    private String documentUniqueId;
    private String reviewDocumentData;
    private List<PageInfo> pages;
    private boolean resume;

    public SaveReviewDataRequest(String reviewDocumentData, String documentUniqueId, List<PageInfo> pages, boolean resume) {
        this.reviewDocumentData = reviewDocumentData;
        this.documentUniqueId = documentUniqueId;
        this.pages = pages;
        this.resume = resume;
    }

    @Override
    public void execute(NoteManager noteManager) throws Exception {
        setResumeRawInputProcessor(resume && noteManager.isDFBForCurrentShape());
        ReviewDocumentData data = JSONObjectParseUtils.parseObject(reviewDocumentData, ReviewDocumentData.class);
        if (data == null) {
            throw ReaderException.noReviewData();
        }

        setVisiblePages(pages);
        ReaderNotePageNameMap pageNameMap = data.getReaderNotePageNameMap();
        List<ReaderFormShapeModel> formShapeModels = data.getReaderFormShapes();
        if (pageNameMap == null && (formShapeModels == null || formShapeModels.size() == 0)) {
            throw ReaderException.noReviewData();
        }

        ensureDocumentOpened(noteManager);
        List<ReaderFormShapeModel> newFormShapeModels = new ArrayList<>();
        for (ReaderFormShapeModel formShapeModel : formShapeModels) {
            if (!ReaderNoteDataProvider.hasFormShape(getContext(), formShapeModel.getShapeUniqueId())) {
                String subPageUniqueId = noteManager.getNoteDocument().getPageUniqueId(formShapeModel.getPageUniqueId(), 0);
                if (!StringUtils.isNullOrEmpty(subPageUniqueId)) {
                    formShapeModel.setSubPageUniqueId(subPageUniqueId);
                }
                formShapeModel.setDocumentUniqueId(documentUniqueId);
                formShapeModel.setColor(Color.RED);
                newFormShapeModels.add(formShapeModel);
            }
        }
        if (newFormShapeModels.size() == 0) {
            throw ReaderException.noReviewData();
        }
        ReaderNoteDataProvider.saveFormShapeList(getContext(), newFormShapeModels);
        noteManager.getNoteDocument().addReviewDataPageMap(pageNameMap);
        noteManager.getNoteDocument().save(getContext(), "title");
        getNoteDataInfo().setContentRendered(renderVisiblePages(noteManager));
    }
}
