package com.onyx.android.sdk.scribble.request;

import com.onyx.android.sdk.scribble.ShapeViewHelper;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;

import java.util.List;

/**
 * Created by zhuzeng on 6/20/16.
 */
public class LoadShapeLibraryRequest extends BaseScribbleRequest {
    private String parentUniqueId;
    private List<NoteModel> list;

    public LoadShapeLibraryRequest(final String id) {
        parentUniqueId = id;
    }

    public void execute(final ShapeViewHelper shapeManager) throws Exception {
        list = NoteDataProvider.loadNoteList(getContext(), parentUniqueId);
    }

    public List<NoteModel> getList() {
        return list;
    }

}
