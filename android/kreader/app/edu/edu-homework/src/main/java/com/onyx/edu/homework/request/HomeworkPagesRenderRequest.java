package com.onyx.edu.homework.request;


import android.graphics.Bitmap;
import android.graphics.Rect;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;
import com.onyx.edu.homework.utils.BitmapUtils;

import java.util.List;

/**
 * Created by lxm on 2017/12/5.
 * load and render shape with scale and offset for homework visible pages.
 */
public class HomeworkPagesRenderRequest extends BaseNoteRequest {

    private Bitmap renderBitmap;
    private boolean base64Bitmap;
    private String base64;

    public HomeworkPagesRenderRequest(final String id, final List<PageInfo> pages, final Rect size, final boolean base64Bitmap) {
        setDocUniqueId(id);
        setAbortPendingTasks(false);
        setViewportSize(size);
        setVisiblePages(pages);
        setPauseInputProcessor(true);
        setResumeInputProcessor(false);
        this.base64Bitmap = base64Bitmap;
    }

    public void execute(final NoteViewHelper parent) throws Exception {
        ensureDocumentOpened(parent);
        updateShapeDataInfo(parent);
        loadShapeData(parent);
        renderVisiblePages(parent);
        if (base64Bitmap) {
            base64 = BitmapUtils.bitmapToBase64(parent.getRenderBitmap());
        }else {
            renderBitmap = Bitmap.createBitmap(parent.getRenderBitmap());
        }
        parent.reset();
    }

    private void loadShapeData(final NoteViewHelper parent) {
        try {
            parent.getNoteDocument().loadShapePages(getContext(), getVisiblePages());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap getRenderBitmap() {
        return renderBitmap;
    }

    public String getBase64() {
        return base64;
    }
}
