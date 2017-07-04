package com.onyx.android.sdk.scribble.request.note;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.AscDescOrder;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.data.SortBy;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

import java.util.List;

/**
 * Created by zhuzeng on 6/20/16.
 */
public class NoteLibraryLoadRequest extends BaseNoteRequest {

    private String parentUniqueId;
    private NoteModel noteModel;
    private List<NoteModel> noteList;
    private volatile boolean loadThumbnail;
    public int thumbnailLimit;
    private
    @SortBy.SortByDef
    int sortBy;
    private
    @AscDescOrder.AscDescOrderDef
    int ascDesc;


    public NoteLibraryLoadRequest(final String id, int thumbLimit) {
        this(id, thumbLimit, SortBy.CREATED_AT, AscDescOrder.DESC);
    }

    public NoteLibraryLoadRequest(final String id, int thumbLimit,
                                  @SortBy.SortByDef int sortBy, @AscDescOrder.AscDescOrderDef int ascDesc) {
        parentUniqueId = id;
        loadThumbnail = true;
        this.sortBy = sortBy;
        this.ascDesc = ascDesc;
        thumbnailLimit = thumbLimit;
        setPauseInputProcessor(true);
        setResumeInputProcessor(false);
    }

    public NoteLibraryLoadRequest(final String id) {
        this(id, Integer.MAX_VALUE);
    }

    public void execute(final NoteViewHelper shapeManager) throws Exception {
        noteModel = NoteDataProvider.load(parentUniqueId);
        if (noteModel != null) {
            noteModel.setExtraAttributes(NoteDataProvider.getNoteAbsolutePath(
                    getContext(), noteModel.getUniqueId(), "/"));
        }
        noteList = NoteDataProvider.loadNoteList(getContext(), parentUniqueId, sortBy, ascDesc);
        if (!loadThumbnail) {
            return;
        }
        int i = 0;
        for (NoteModel noteModel : noteList) {
            if (noteModel.isDocument() && i < thumbnailLimit) {
                loadThumbnail(noteModel);
                i++;
            }
            if (!noteModel.isDocument()){
                loadSubDocCount(noteModel);
            }
        }
    }

    private void loadThumbnail(NoteModel noteModel) {
        noteModel.setThumbnail(NoteDataProvider.loadThumbnail(getContext(), noteModel.getUniqueId()));
    }

    private void loadSubDocCount(NoteModel noteModel) {
        List<NoteModel> subNoteList = NoteDataProvider.loadNoteList(getContext(), noteModel.getUniqueId(), sortBy, ascDesc);
        noteModel.setSubDocCount(subNoteList == null ? 0 : subNoteList.size());
    }

    public List<NoteModel> getNoteList() {
        return noteList;
    }

    public NoteModel getNoteModel() {
        return noteModel;
    }

}
