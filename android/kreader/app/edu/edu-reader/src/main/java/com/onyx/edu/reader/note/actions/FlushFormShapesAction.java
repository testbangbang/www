package com.onyx.edu.reader.note.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.edu.reader.note.NoteManager;
import com.onyx.edu.reader.note.request.FlushFormShapesRequest;
import com.onyx.edu.reader.ui.actions.BaseAction;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

import java.util.List;

/**
 * Created by ming on 2017/6/6.
 */

public class FlushFormShapesAction extends BaseAction {

    private List<Shape> shapes;
    private boolean resume;

    public FlushFormShapesAction(List<Shape> shapes, boolean resume) {
        this.shapes = shapes;
        this.resume = resume;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, BaseCallback baseCallback) {
        FlushFormShapesRequest flushFormShapesRequest = new FlushFormShapesRequest(shapes, resume);
        final NoteManager noteManager = readerDataHolder.getNoteManager();
        noteManager.submit(readerDataHolder.getContext(), flushFormShapesRequest, baseCallback);
    }
}
