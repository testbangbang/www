package com.onyx.edu.reader.note.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.edu.reader.note.data.ReaderNotePageNameMap;
import com.onyx.edu.reader.note.model.ReaderFormShapeModel;
import com.onyx.edu.reader.note.request.SaveReviewDataRequest;
import com.onyx.edu.reader.ui.actions.BaseAction;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;
import com.onyx.edu.reader.ui.events.ShapeRenderFinishEvent;

import java.util.List;

/**
 * Created by ming on 2017/6/12.
 */

public class SaveReviewDataAction extends BaseAction {

    private String documentUniqueId;
    private String reviewDocumentData;

    public SaveReviewDataAction(String reviewDocumentData, String documentUniqueId) {
        this.documentUniqueId = documentUniqueId;
        this.reviewDocumentData = reviewDocumentData;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback baseCallback) {
        SaveReviewDataRequest saveReviewDataRequest = new SaveReviewDataRequest(reviewDocumentData, documentUniqueId, readerDataHolder.getVisiblePages());
        readerDataHolder.getNoteManager().submit(readerDataHolder.getContext(), saveReviewDataRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e == null) {
                    readerDataHolder.getEventBus().post(ShapeRenderFinishEvent.shapeReadyEvent());
                }
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }
}
