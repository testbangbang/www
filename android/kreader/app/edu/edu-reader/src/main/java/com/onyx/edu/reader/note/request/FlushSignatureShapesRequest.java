package com.onyx.edu.reader.note.request;

import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.wrapper.Reader;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.edu.reader.note.NoteManager;
import com.onyx.edu.reader.note.data.ReaderShapeFactory;
import com.onyx.edu.reader.note.model.ReaderFormShapeModel;
import com.onyx.edu.reader.note.model.ReaderNoteDataProvider;
import com.onyx.edu.reader.note.model.SignatureShapeModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ming on 2017/8/1.
 */

public class FlushSignatureShapesRequest extends BaseReaderRequest {

    private String accountId;
    private String documentId;

    public FlushSignatureShapesRequest(String accountId, String documentId) {
        this.accountId = accountId;
        this.documentId = documentId;
    }

    @Override
    public void execute(Reader reader) throws Exception {
        List<SignatureShapeModel> models = new ArrayList<>();
        List<ReaderFormShapeModel> formShapeModels = ReaderNoteDataProvider.loadFormShapeList(getContext(), documentId);
        List<Shape> shapeList = new ArrayList<>();
        for (ReaderFormShapeModel formShapeModel : formShapeModels) {
            shapeList.add(ReaderShapeFactory.shapeFromFormModel(formShapeModel));
        }
        for (Shape shape : shapeList) {
            models.add(ReaderShapeFactory.signatureModelFromShape(shape, accountId));
        }
        ReaderNoteDataProvider.removeSignatureShapes(getContext(), accountId);
        ReaderNoteDataProvider.saveSignatureShapeList(getContext(), models);
    }
}
