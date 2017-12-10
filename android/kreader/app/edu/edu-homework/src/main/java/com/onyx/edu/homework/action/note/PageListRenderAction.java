package com.onyx.edu.homework.action.note;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.navigation.PageListRenderRequest;
import com.onyx.edu.homework.base.BaseNoteAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lxm on 2017/12/8.
 */

public class PageListRenderAction extends BaseNoteAction {

    private List<String> pageUniqueIds;
    private String douId;
    private Rect size;
    private List<Bitmap> bitmaps = new ArrayList<>();

    public PageListRenderAction(List<String> pageUniqueIds, String douId, Rect size) {
        this.pageUniqueIds = pageUniqueIds;
        this.douId = douId;
        this.size = size;
    }

    @Override
    public void execute(NoteViewHelper noteViewHelper, final BaseCallback baseCallback) {
        if (pageUniqueIds == null || pageUniqueIds.size() == 0) {
            return;
        }
        String pageUniqueId = pageUniqueIds.remove(0);
        List<PageInfo> pageInfoList = new ArrayList<>();
        PageInfo pageInfo = new PageInfo(pageUniqueId, size.width(), size.height());
        pageInfo.updateDisplayRect(new RectF(0, 0, size.width(), size.height()));
        pageInfoList.add(pageInfo);
        final PageListRenderRequest renderRequest = new PageListRenderRequest(douId, pageInfoList, size, false, true);
        noteViewHelper.submit(getAppContext(), renderRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    return;
                }
                bitmaps.add(renderRequest.getRenderBitmap());
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }

    public List<Bitmap> getBitmaps() {
        return bitmaps;
    }
}
