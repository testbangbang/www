package com.onyx.android.sdk.scribble.asyncrequest.note;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;

import java.util.List;

/**
 * Created by zhuzeng on 6/22/16.
 */
public class NoteMoveRequest extends AsyncBaseNoteRequest {
    private String parentID;
    private List<String> uniqueIdList;
    private boolean checkNameLegality;
    private boolean checkThisLevelOnly;
    private boolean distinguishFileType;

    public NoteMoveRequest(String parentID, List<String> uniqueIdList,
                           boolean checkNameLegality, boolean checkThisLevelOnly, boolean distinguishFileType) {
        this.parentID = parentID;
        this.uniqueIdList = uniqueIdList;
        this.checkNameLegality = checkNameLegality;
        this.checkThisLevelOnly = checkThisLevelOnly;
        this.distinguishFileType = distinguishFileType;
        setPauseInputProcessor(true);
        setResumeInputProcessor(false);
    }

    public void execute(final NoteViewHelper shapeManager) throws Exception {
        for (String uniqueId : uniqueIdList) {
            if (checkNameLegality) {
                NoteModel noteModel = NoteDataProvider.load(getContext(), uniqueId);
                if (NoteDataProvider.checkNoteNameLegality(noteModel.getUniqueId(), noteModel.getTitle(), parentID,
                        noteModel.getType(), checkThisLevelOnly, distinguishFileType, true)) {
                    NoteDataProvider.moveNote(getContext(), uniqueId, parentID);
                }
            } else {
                NoteDataProvider.moveNote(getContext(), uniqueId, parentID);
            }

        }
    }

}
