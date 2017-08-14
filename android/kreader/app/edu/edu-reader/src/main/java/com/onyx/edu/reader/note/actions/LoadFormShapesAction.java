package com.onyx.edu.reader.note.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.edu.reader.note.model.ReaderFormShapeModel;
import com.onyx.edu.reader.note.request.LoadFormShapesRequest;
import com.onyx.edu.reader.ui.actions.BaseAction;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

import java.util.List;

/**
 * Created by lxm on 2017/8/9.
 */

public class LoadFormShapesAction extends BaseAction {

    private List<ReaderFormShapeModel> readerFormShapeModels;

    @Override
    public void execute(ReaderDataHolder readerDataHolder, final BaseCallback baseCallback) {
        final LoadFormShapesRequest shapesRequest = new LoadFormShapesRequest();
        readerDataHolder.submitNonRenderRequest(shapesRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                readerFormShapeModels = shapesRequest.getReaderFormShapeModels();
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }

    public List<ReaderFormShapeModel> getReaderFormShapeModels() {
        return readerFormShapeModels;
    }
}
