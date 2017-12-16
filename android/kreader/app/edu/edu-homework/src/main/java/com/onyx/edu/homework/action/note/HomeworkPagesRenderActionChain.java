package com.onyx.edu.homework.action.note;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.android.annotations.NonNull;
import com.android.annotations.Nullable;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.edu.homework.base.BaseNoteAction;
import com.onyx.edu.homework.base.NoteActionChain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lxm on 2017/12/8.
 */

public class HomeworkPagesRenderActionChain extends BaseNoteAction {

    private List<String> docIds;
    private Rect size;
    private Map<String, List<Bitmap>> pageMap;
    private int pageCount = -1;
    private Map<String, List<String>> unRenderPageUniqueMap;

    public HomeworkPagesRenderActionChain(String docId, Rect size, int pageCount) {
        this.docIds = new ArrayList<>();
        docIds.add(docId);
        this.size = size;
        this.pageCount = pageCount;
    }

    public HomeworkPagesRenderActionChain(String docId, Rect size) {
        this(docId, size, -1);
    }


    @Override
    public void execute(final NoteViewHelper noteViewHelper, final BaseCallback baseCallback) {
        NoteActionChain chain = new NoteActionChain(true);
        GetPageUniqueIdsAction pageUniqueIdsAction = new GetPageUniqueIdsAction(docIds);
        final HomeworkPagesRenderAction listRenderAction = new HomeworkPagesRenderAction(pageUniqueIdsAction.getPageUniqueMap(),
                size,
                pageCount,
                false);
        chain.addAction(pageUniqueIdsAction);
        chain.addAction(listRenderAction);
        chain.execute(noteViewHelper, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                unRenderPageUniqueMap = listRenderAction.getUnRenderPageUniqueMap();
                pageMap = listRenderAction.getPageBitmaps();
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }

    @NonNull
    public Map<String, List<Bitmap>> getPageMap() {
        return pageMap;
    }

    @Nullable
    public Map<String, List<String>> getUnRenderPageUniqueMap() {
        return unRenderPageUniqueMap;
    }
}
