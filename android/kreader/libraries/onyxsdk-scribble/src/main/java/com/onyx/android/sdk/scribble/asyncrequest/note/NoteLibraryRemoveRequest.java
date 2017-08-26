package com.onyx.android.sdk.scribble.asyncrequest.note;

import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;
import com.onyx.android.sdk.scribble.data.NoteModel;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by zhuzeng on 6/23/16.
 */
public class NoteLibraryRemoveRequest extends AsyncBaseNoteRequest {
    private List<String> uniqueIdList;
    private List<NoteModel> noteList;
    private List<String> targetRemoveList;

    public NoteLibraryRemoveRequest(List<String> targetIDList) {
        uniqueIdList = new ArrayList<>();
        uniqueIdList.addAll(targetIDList);
        setPauseInputProcessor(true);
        setResumeInputProcessor(false);
    }

    @Override
    public void execute(final NoteManager shapeManager) throws Exception {
        noteList = NoteDataProvider.loadAllNoteLibraryList();
        targetRemoveList = new ArrayList<>();
        for (String id : uniqueIdList) {
            if (NoteDataProvider.load(getContext(), id).getType() == NoteModel.TYPE_DOCUMENT) {
                NoteDataProvider.remove(getContext(), id);
                continue;
            }
            for (NoteModel model : noteList) {
                if (NoteDataProvider.isChildLibrary(getContext(),
                        model.getUniqueId(), id) ||
                        model.getUniqueId().equals(id)) {
                    targetRemoveList.add(model.getUniqueId());
                }
            }
        }
        if (targetRemoveList.size() > 0) {
            for (String id : targetRemoveList) {
                NoteDataProvider.remove(getContext(), id);
            }
        }
    }


}
