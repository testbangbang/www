package com.onyx.kreader.note.request;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.data.PageRange;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.kreader.note.NoteManager;
import com.onyx.kreader.note.data.ReaderNotePage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ming on 16/9/24.
 */
public class GetAllShapesRequest extends ReaderBaseNoteRequest{

    private HashMap<String, PageInfo> subPageMap = new HashMap<>();
    private List<Shape> shapes = new ArrayList<>();

    @Override
    public void execute(NoteManager noteManager) throws Exception {
        List<PageInfo> pageList = noteManager.getNoteDocument().getNoEmptyPageList(getContext());
        for (PageInfo page : pageList) {
            PageRange range = PageRange.create(page.getName(), page.getName());
            ReaderNotePage notePage = noteManager.getNoteDocument().loadPage(getContext(), range, page.getSubPage());
            if (notePage != null) {
                if (!notePage.isLoaded()) {
                    notePage.loadPage(getContext());
                }
                subPageMap.put(notePage.getSubPageUniqueId(), page);
                shapes.addAll(notePage.getShapeList());
            }
        }
    }

    public HashMap<String, PageInfo> getSubPageMap() {
        return subPageMap;
    }

    public List<Shape> getShapes() {
        return shapes;
    }
}
