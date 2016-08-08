package com.onyx.android.sdk.scribble.request.note;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by zhuzeng on 6/23/16.
 */
public class NoteLibraryRemoveRequest extends BaseNoteRequest {
    private List<String> uniqueIdList;
    private List<NoteModel> noteList;
    private List<String> targetRemoveList;

    public NoteLibraryRemoveRequest(List<String> targetIDList) {
        uniqueIdList = new ArrayList<>();
        uniqueIdList.addAll(targetIDList);
        setPauseInputProcessor(true);
        setResumeInputProcessor(false);
    }

    public void execute(final NoteViewHelper shapeManager) throws Exception {
        noteList = NoteDataProvider.loadAllNoteLibraryList();
        targetRemoveList = new ArrayList<>();
        for (String id : uniqueIdList) {
            for (NoteModel model : noteList) {
                if (NoteDataProvider.isChildLibrary(getContext(),
                        model.getUniqueId(), id) ||
                        model.getUniqueId().equals(id)) {
                    targetRemoveList.add(model.getUniqueId());
                }
            }
        }
        for (String id : targetRemoveList) {
            NoteDataProvider.remove(getContext(), id);
        }
    }


}
