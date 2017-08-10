package com.onyx.edu.reader.note.request;

import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.wrapper.Reader;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.edu.reader.note.model.ReaderFormShapeModel;
import com.onyx.edu.reader.note.model.ReaderNoteDataProvider;

import java.util.List;

/**
 * Created by lxm on 2017/8/9.
 */

public class LoadFormShapesRequest extends BaseReaderRequest {

    private List<ReaderFormShapeModel> readerFormShapeModels;

    @Override
    public void execute(Reader reader) throws Exception {
        readerFormShapeModels = ReaderNoteDataProvider.loadFormShapeList(getContext(), reader.getDocumentMd5());
    }

    public List<ReaderFormShapeModel> getReaderFormShapeModels() {
        return readerFormShapeModels;
    }
}
