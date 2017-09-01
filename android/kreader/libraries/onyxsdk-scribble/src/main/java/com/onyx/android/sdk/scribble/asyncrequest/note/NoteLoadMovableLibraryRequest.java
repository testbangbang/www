package com.onyx.android.sdk.scribble.asyncrequest.note;

import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by solskjaer49 on 6/20/16 16:46.
 */
public class NoteLoadMovableLibraryRequest extends AsyncBaseNoteRequest {
    private String currentLibID;
    private List<String> excludeIDList;
    private List<NoteModel> noteList;

    public NoteLoadMovableLibraryRequest(String currentLibID, List<String> excludeIDList) {
        this.currentLibID = currentLibID;
        this.excludeIDList = excludeIDList;
        setPauseInputProcessor(true);
        setResumeInputProcessor(false);
    }

    @Override
    public void execute(final NoteManager shapeManager) throws Exception {
        noteList = NoteDataProvider.loadAllNoteLibraryList();
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
        if (StringUtils.isNotBlank(currentLibID)) {
            NoteModel topLevelFakeModel = new NoteModel();
            topLevelFakeModel.setUniqueId(null);
            topLevelFakeModel.setExtraAttributes("\\");
            noteList.add(0,topLevelFakeModel);
        }
    }

    public List<NoteModel> getNoteList() {
        return noteList;
    }

}
