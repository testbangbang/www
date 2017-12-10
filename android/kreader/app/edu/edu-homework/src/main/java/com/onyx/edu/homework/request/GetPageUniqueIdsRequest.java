package com.onyx.edu.homework.request;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

import java.util.List;

/**
 * Created by lxm on 2017/12/6.
 */

public class GetPageUniqueIdsRequest extends BaseNoteRequest {

    private volatile String uniqueId;
    private List<String> pageUniqueIds;

    public GetPageUniqueIdsRequest(String uniqueId) {
        this.uniqueId = uniqueId;
        setPauseInputProcessor(false);
        setResumeInputProcessor(false);
    }

    @Override
    public void execute(NoteViewHelper helper) throws Exception {
        super.execute(helper);
        NoteModel noteModel = NoteDataProvider.load(uniqueId);
        pageUniqueIds = noteModel.getPageNameList().getPageNameList();
    }

    public List<String> getPageUniqueIds() {
        return pageUniqueIds;
    }
}
