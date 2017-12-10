package com.onyx.edu.homework.action.note;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.edu.homework.base.BaseAction;
import com.onyx.edu.homework.base.BaseNoteAction;
import com.onyx.edu.homework.base.NoteActionChain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lxm on 2017/12/8.
 */

public class PageListRenderActionChain extends BaseNoteAction {

    private String douId;
    private Rect size;
    private List<Bitmap> bitmaps;

    public PageListRenderActionChain(String douId, Rect size) {
        this.douId = douId;
        this.size = size;
    }

    @Override
    public void execute(NoteViewHelper noteViewHelper, final BaseCallback baseCallback) {
        NoteActionChain chain = new NoteActionChain(true);
        GetPageUniqueIdsAction pageUniqueIdsAction = new GetPageUniqueIdsAction(douId);
        final PageListRenderAction listRenderAction = new PageListRenderAction(pageUniqueIdsAction.getPageUniqueIds(), douId, size);
        chain.addAction(pageUniqueIdsAction);
        chain.addAction(listRenderAction);
        chain.execute(noteViewHelper, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                bitmaps = listRenderAction.getBitmaps();
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }

    public List<Bitmap> getBitmaps() {
        return bitmaps;
    }
}
