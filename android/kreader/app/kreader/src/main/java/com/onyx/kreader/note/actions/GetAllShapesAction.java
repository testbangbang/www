package com.onyx.kreader.note.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.kreader.note.NoteManager;
import com.onyx.kreader.note.request.GetAllShapesRequest;
import com.onyx.kreader.note.request.GetNotePageListRequest;
import com.onyx.kreader.ui.actions.BaseAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;

import java.util.List;

/**
 * Created by ming on 16/9/23.
 */
public class GetAllShapesAction extends BaseAction{

    List<Shape> shapes;

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        final GetAllShapesRequest shapeRequest = new GetAllShapesRequest();
        readerDataHolder.getNoteManager().submit(readerDataHolder.getContext(), shapeRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                shapes = shapeRequest.getShapes();
                BaseCallback.invoke(callback, request, e);
            }
        });
    }

    public List<Shape> getShapes() {
        return shapes;
    }
}
