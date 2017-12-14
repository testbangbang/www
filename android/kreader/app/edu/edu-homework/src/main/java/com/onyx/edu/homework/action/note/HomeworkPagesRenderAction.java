package com.onyx.edu.homework.action.note;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.edu.homework.base.BaseNoteAction;
import com.onyx.edu.homework.request.HomeworkPagesRenderRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lxm on 2017/12/8.
 */

public class HomeworkPagesRenderAction extends BaseNoteAction {

    private Map<String, List<String>> pageUniqueMap;
    private Rect size;
    private boolean base64Bitmap;
    private Map<String, List<Bitmap>> pageBitmaps = new HashMap<>();
    private Map<String, List<String>> pageBase64s = new HashMap<>();
    private List<String> documentIds = new ArrayList<>();
    private int pageCount = -1;
    private int documentRenderCount = 0;

    public HomeworkPagesRenderAction(Map<String, List<String>> pageUniqueMap,
                                     Rect size,
                                     int pageCount,
                                     boolean base64Bitmap) {
        this.pageUniqueMap = pageUniqueMap;
        this.size = size;
        this.base64Bitmap = base64Bitmap;
        this.pageCount = pageCount;
    }

    public HomeworkPagesRenderAction(Map<String, List<String>> pageUniqueMap,
                                     Rect size,
                                     boolean base64Bitmap) {
        this(pageUniqueMap, size, -1, base64Bitmap);
    }

    @Override
    public void execute(final NoteViewHelper noteViewHelper, final BaseCallback baseCallback) {
        if (pageUniqueMap != null) {
            documentIds.addAll(pageUniqueMap.keySet());
        }
        renderPageBitmap(noteViewHelper, baseCallback);
    }

    private void renderPageBitmap(final NoteViewHelper noteViewHelper, final BaseCallback baseCallback) {
        if (isFinished(noteViewHelper)) {
            BaseCallback.invoke(baseCallback, null, null);
            return;
        }
        final String docId = documentIds.get(0);
        List<String> pageUniqueIds = pageUniqueMap.get(docId);

        String pageUniqueId = pageUniqueIds.remove(0);
        List<PageInfo> pageInfoList = new ArrayList<>();
        PageInfo pageInfo = new PageInfo(pageUniqueId, size.width(), size.height());
        pageInfo.updateDisplayRect(new RectF(0, 0, size.width(), size.height()));
        pageInfoList.add(pageInfo);
        final HomeworkPagesRenderRequest renderRequest = new HomeworkPagesRenderRequest(docId, pageInfoList, size, base64Bitmap);
        noteViewHelper.submit(getAppContext(), renderRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    BaseCallback.invoke(baseCallback, request, e);
                    return;
                }
                documentRenderCount++;
                if (base64Bitmap) {
                    addBase64Bitmap(docId, renderRequest.getBase64());
                }else {
                    addBitmap(docId, renderRequest.getRenderBitmap());
                }
                renderPageBitmap(noteViewHelper, baseCallback);
            }
        });
    }

    private boolean isFinished(final NoteViewHelper noteViewHelper) {
        if (documentIds == null || documentIds.size() == 0) {
            return true;
        }
        String documentId = documentIds.get(0);
        List<String> pageUniqueIds = pageUniqueMap.get(documentId);
        if (pageUniqueIds == null || pageUniqueIds.size() == 0 || documentRenderCount == pageCount) {
            documentIds.remove(0);
            documentRenderCount = 0;
            noteViewHelper.reset();
        }
        return (documentIds == null || documentIds.size() == 0);
    }

    private void addBitmap(String docId, Bitmap bitmap) {
        List<Bitmap> bitmaps;
        if (pageBitmaps.containsKey(docId)) {
            bitmaps = pageBitmaps.get(docId);
            bitmaps.add(bitmap);
        }else {
            bitmaps = new ArrayList<>();
            bitmaps.add(bitmap);
            pageBitmaps.put(docId, bitmaps);
        }
    }

    private void addBase64Bitmap(String docId, String base64) {
        List<String> base64s;
        if (pageBase64s.containsKey(docId)) {
            base64s = pageBase64s.get(docId);
            base64s.add(base64);
        }else {
            base64s = new ArrayList<>();
            base64s.add(base64);
            pageBase64s.put(docId, base64s);
        }
    }

    public Map<String, List<Bitmap>> getPageBitmaps() {
        return pageBitmaps;
    }

    public Map<String, List<String>> getPageBase64s() {
        return pageBase64s;
    }
}
