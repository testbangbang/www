package com.onyx.edu.homework.request;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lxm on 2017/12/6.
 */

public class GetPageUniqueIdsRequest extends BaseNoteRequest {

    private volatile List<String> docIds;
    private Map<String, List<String>> pageUniqueMap;

    public GetPageUniqueIdsRequest(List<String> docIds) {
        this.docIds = docIds;
        setPauseInputProcessor(false);
        setResumeInputProcessor(false);
        setRender(false);
    }

    @Override
    public void execute(NoteViewHelper helper) throws Exception {
        super.execute(helper);
        if (docIds == null) {
            return;
        }
        pageUniqueMap = new HashMap<>();
        for (String docId : docIds) {
            NoteModel noteModel = NoteDataProvider.load(docId);
            pageUniqueMap.put(docId, noteModel.getPageNameList().getPageNameList());
        }
    }

    public Map<String, List<String>> getPageUniqueMap() {
        return pageUniqueMap;
    }
}
