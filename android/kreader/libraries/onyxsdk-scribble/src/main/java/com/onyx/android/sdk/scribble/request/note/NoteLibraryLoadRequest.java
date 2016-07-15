package com.onyx.android.sdk.scribble.request.note;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

import java.util.List;

/**
 * Created by zhuzeng on 6/20/16.
 */
public class NoteLibraryLoadRequest extends BaseNoteRequest {

    private String parentUniqueId;
    private List<NoteModel> noteList;
    private volatile boolean loadThumbnail;
    public int thumbnailLimit;

    public NoteLibraryLoadRequest(final String id, int thumbLimit) {
        parentUniqueId = id;
        loadThumbnail = true;
        thumbnailLimit = thumbLimit;
        setPauseInputProcessor(true);
        setResumeInputProcessor(false);
    }

    public NoteLibraryLoadRequest(final String id) {
        this(id, Integer.MAX_VALUE);
    }

    public void execute(final NoteViewHelper shapeManager) throws Exception {
        noteList = NoteDataProvider.loadNoteList(getContext(), parentUniqueId);
        if (!loadThumbnail) {
            return;
        }
        int i = 0;
        for (NoteModel noteModel : noteList) {
            if (noteModel.isDocument() && i < thumbnailLimit) {
                noteModel.setThumbnail(NoteDataProvider.loadThumbnail(getContext(), noteModel.getUniqueId()));
                i++;
            }
        }
    }

    public List<NoteModel> getNoteList() {
        return noteList;
    }

}
