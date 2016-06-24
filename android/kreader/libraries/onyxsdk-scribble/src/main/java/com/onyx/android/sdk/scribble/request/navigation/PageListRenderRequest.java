package com.onyx.android.sdk.scribble.request.navigation;


import android.graphics.*;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.BuildConfig;
import com.onyx.android.sdk.scribble.ShapeViewHelper;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;
import com.onyx.android.sdk.scribble.data.NotePage;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.utils.TestUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by zengzhu on 4/18/16.
 * load and render shape with scale and offset for specified visible pages.
 */
public class PageListRenderRequest extends BaseNoteRequest {

    public PageListRenderRequest(final List<PageInfo> pages, final Rect size) {
        setAbortPendingTasks();
        setViewportSize(size);
        setVisiblePages(pages);
    }

    public void execute(final ShapeViewHelper parent) throws Exception {
        loadShapeData(parent);
        renderVisiblePages(parent);
        updateShapeDataInfo(parent);
    }

    public void loadShapeData(final ShapeViewHelper parent) {
        try {
            parent.getNoteDocument().loadShapePages(getContext(), getVisiblePages());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
