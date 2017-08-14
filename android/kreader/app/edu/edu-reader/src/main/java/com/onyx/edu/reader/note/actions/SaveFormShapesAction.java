package com.onyx.edu.reader.note.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.edu.reader.note.model.ReaderFormShapeModel;
import com.onyx.edu.reader.note.request.SaveFormShapesRequest;
import com.onyx.edu.reader.ui.actions.BaseAction;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

import java.util.List;

/**
 * Created by lxm on 2017/8/10.
 */

public class SaveFormShapesAction extends BaseAction {

    private List<ReaderFormShapeModel> shapeModels;

    public SaveFormShapesAction(List<ReaderFormShapeModel> shapeModels) {
        this.shapeModels = shapeModels;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, BaseCallback baseCallback) {
        SaveFormShapesRequest saveFormShapesRequest = new SaveFormShapesRequest(shapeModels);
        readerDataHolder.getNoteManager().submit(readerDataHolder.getContext(), saveFormShapesRequest, baseCallback);
    }
}
