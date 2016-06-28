package com.onyx.android.sdk.scribble.request.note;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

import java.util.List;

/**
 * Created by zhuzeng on 6/22/16.
 */
public class NoteMoveRequest extends BaseNoteRequest {
    String parentID;
    List<String> uniqueIdList;

    public NoteMoveRequest(String parentID, List<String> uniqueIdList) {
        this.parentID = parentID;
        this.uniqueIdList = uniqueIdList;
    }

    public void execute(final NoteViewHelper shapeManager) throws Exception {
        for (String uniqueId : uniqueIdList) {
            NoteDataProvider.moveNote(getContext(), uniqueId, parentID);
        }
    }

}
