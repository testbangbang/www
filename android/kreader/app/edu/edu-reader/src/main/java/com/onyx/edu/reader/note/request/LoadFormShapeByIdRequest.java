package com.onyx.edu.reader.note.request;

import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.wrapper.Reader;
import com.onyx.edu.reader.note.model.ReaderFormShapeModel;
import com.onyx.edu.reader.note.model.ReaderNoteDataProvider;

/**
 * Created by lxm on 2017/8/22.
 */

public class LoadFormShapeByIdRequest extends BaseReaderRequest {

    private String documentUniqueId;
    private String formId;

    private ReaderFormShapeModel formShapeModel;

    public LoadFormShapeByIdRequest(String documentUniqueId, String formId) {
        this.documentUniqueId = documentUniqueId;
        this.formId = formId;
    }

    @Override
    public void execute(Reader reader) throws Exception {
        formShapeModel = ReaderNoteDataProvider.loadFormShape(getContext(), documentUniqueId, formId);
    }

    public ReaderFormShapeModel getFormShapeModel() {
        return formShapeModel;
    }
}
