package com.onyx.android.sdk.scribble.request.note;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by zhuzeng on 6/23/16.
 */
public class NoteLibraryRemoveRequest extends BaseNoteRequest {
    List<String> uniqueIdList;

    public NoteLibraryRemoveRequest(List<String> targetIDList) {
        uniqueIdList = new ArrayList<>();
        uniqueIdList.addAll(targetIDList);
        setPauseInputProcessor(true);
        setResumeInputProcessor(false);
    }

    public void execute(final NoteViewHelper shapeManager) throws Exception {
        //TODO:should use batch remove method?
        for (String uniqueId : uniqueIdList) {
            NoteDataProvider.remove(getContext(), uniqueId);
        }
    }


}
