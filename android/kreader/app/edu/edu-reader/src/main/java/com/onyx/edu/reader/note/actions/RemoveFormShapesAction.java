package com.onyx.edu.reader.note.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.edu.reader.note.request.RemoveFormShapesRequest;
import com.onyx.edu.reader.ui.actions.BaseAction;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

import java.util.List;

/**
 * Created by lxm on 2017/8/10.
 */

public class RemoveFormShapesAction extends BaseAction {

    private List<String> shapeIds;

    public RemoveFormShapesAction(List<String> shapeIds) {
        this.shapeIds = shapeIds;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, BaseCallback baseCallback) {
        RemoveFormShapesRequest removeFormShapesRequest = new RemoveFormShapesRequest(shapeIds);
        readerDataHolder.getNoteManager().submit(readerDataHolder.getContext(), removeFormShapesRequest, baseCallback);
    }
}
