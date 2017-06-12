package com.onyx.kreader.note.request;

import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.kreader.note.NoteManager;
import com.onyx.kreader.note.data.ReaderNotePage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ming on 16/9/24.
 */
public class GetAllShapesRequest extends ReaderBaseNoteRequest{

    private List<Shape> shapes = new ArrayList<>();

    @Override
    public void execute(NoteManager noteManager) throws Exception {
        List<String> pageList = noteManager.getNoteDocument().getPageList();
        for (String page : pageList) {
            ReaderNotePage notePage = noteManager.getNoteDocument().loadPage(getContext(), page, 0);
            if (notePage != null) {
                if (!notePage.isLoaded()) {
                    notePage.loadPage(getContext());
                }
                shapes.addAll(notePage.getShapeList());
            }
        }
    }

    public List<Shape> getShapes() {
        return shapes;
    }
}
