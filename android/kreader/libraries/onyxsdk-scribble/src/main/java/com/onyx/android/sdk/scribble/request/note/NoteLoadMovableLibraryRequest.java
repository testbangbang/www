package com.onyx.android.sdk.scribble.request.note;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by solskjaer49 on 6/20/16 16:46.
 */
public class NoteLoadMovableLibraryRequest extends BaseNoteRequest {
    public NoteLoadMovableLibraryRequest(String currentLibID, List<String> excludeIDList) {
        this.currentLibID = currentLibID;
        this.excludeIDList = excludeIDList;
    }

    private String currentLibID;
    private List<String> excludeIDList;
    private List<NoteModel> noteList;

    public void execute(final NoteViewHelper shapeManager) throws Exception {
        noteList = NoteDataProvider.loadMovableNoteLibraryList(getContext());
        List<NoteModel> dataList = new ArrayList<>();
        dataList.addAll(noteList);
        for (String id : excludeIDList) {
            for (NoteModel model : dataList) {
                if (NoteDataProvider.isChildLibrary(getContext(),
                        model.getUniqueId(), id) ||
                        model.getUniqueId().equals(id) ||
                        model.getUniqueId().equals(currentLibID)) {
                    noteList.remove(model);
                }
            }
        }
        for (NoteModel model : noteList) {
            model.setExtraAttributes(NoteDataProvider.getNoteAbsolutePath(getContext(), model.getUniqueId(), "\\"));
        }
    }

    public List<NoteModel> getNoteList() {
        return noteList;
    }

}
