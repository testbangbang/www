package com.onyx.android.sdk.scribble.request.note;

import com.onyx.android.sdk.scribble.ShapeViewHelper;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;
import com.onyx.android.sdk.scribble.request.BaseScribbleRequest;

import java.util.List;

/**
 * Created by zhuzeng on 6/20/16.
 */
public class NoteLibraryLoadRequest extends BaseScribbleRequest {

    private String parentUniqueId;
    private List<NoteModel> noteList;
    private volatile boolean loadThumbnail;

    public NoteLibraryLoadRequest(final String id) {
        parentUniqueId = id;
        loadThumbnail = true;
    }

    public void execute(final ShapeViewHelper shapeManager) throws Exception {
        noteList = NoteDataProvider.loadNoteList(getContext(), parentUniqueId);
        if (!loadThumbnail) {
            return;
        }
        for(NoteModel noteModel : noteList) {
            if (noteModel.isDocument()) {
                noteModel.setThumbnail(NoteDataProvider.loadThumbnail(getContext(), noteModel.getUniqueId()));
            } else {
                // use default drawable
            }
        }
    }

    public List<NoteModel> getNoteList() {
        return noteList;
    }

}
