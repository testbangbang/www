package com.onyx.android.sdk.scribble.asyncrequest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;

import com.hanvon.core.Algorithm;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.common.request.RequestManager;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.R;
import com.onyx.android.sdk.scribble.data.NoteBackgroundType;
import com.onyx.android.sdk.scribble.data.NoteDrawingArgs;
import com.onyx.android.sdk.scribble.data.NotePage;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.request.ShapeDataInfo;
import com.onyx.android.sdk.scribble.shape.RenderContext;
import com.onyx.android.sdk.scribble.utils.DeviceConfig;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.utils.TestUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 6/3/16.
 * Requests are used in standalone application or separate page rendering from
 * shape rendering.
 */
public class AsyncBaseNoteRequest extends BaseRequest {

    private volatile ShapeDataInfo shapeDataInfo;
    private String docUniqueId;
    private String parentLibraryId;
    private Rect viewportSize;
    private List<PageInfo> visiblePages = new ArrayList<>();

    private boolean pauseInputProcessor = true;
    private boolean resumeInputProcessor = false;
    private volatile boolean renderToBitmap = true;
    private volatile boolean renderToScreen = true;

    private String identifier;

    public AsyncBaseNoteRequest() {
        setAbortPendingTasks(true);
    }

    public boolean isResumeInputProcessor() {
        return resumeInputProcessor;
    }

    public void setResumeInputProcessor(boolean resumeInputProcessor) {
        this.resumeInputProcessor = resumeInputProcessor;
    }

    public boolean isPauseInputProcessor() {
        return pauseInputProcessor;
    }

    public void setPauseInputProcessor(boolean pauseInputProcessor) {
        this.pauseInputProcessor = pauseInputProcessor;
    }

    public boolean isRenderToBitmap() {
        return renderToBitmap;
    }

    public void setRenderToBitmap(boolean render) {
        renderToBitmap = render;
    }

    public boolean isRenderToScreen() {
        return renderToScreen;
    }

    public void setRenderToScreen(boolean render) {
        renderToScreen = render;
    }

    public void setRender(boolean render) {
        setRenderToBitmap(render);
        setRenderToScreen(render);
    }

    public void setDocUniqueId(final String id) {
        docUniqueId = id;
    }

    public final String getDocUniqueId() {
        return docUniqueId;
    }

    public String getParentLibraryId() {
        return parentLibraryId;
    }

    public void setParentLibraryId(String parentLibraryId) {
        this.parentLibraryId = parentLibraryId;
    }

    public void setViewportSize(final Rect size) {
        viewportSize = size;
    }

    public final Rect getViewportSize() {
        return viewportSize;
    }

    public void setVisiblePages(final List<PageInfo> pages) {
        visiblePages.addAll(pages);
    }

    public final List<PageInfo> getVisiblePages() {
        return visiblePages;
    }

    public void beforeExecute(final NoteManager noteManager) {
        noteManager.getRequestManager().acquireWakeLock(getContext(), getClass().getSimpleName());
        if (isPauseInputProcessor()) {
            noteManager.pauseDrawing();
        }
        benchmarkStart();
        invokeStartCallback(noteManager.getRequestManager());
    }

    private void invokeStartCallback(final RequestManager requestManager) {
        if (getCallback() == null) {
            return;
        }
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                getCallback().start(AsyncBaseNoteRequest.this);
            }
        };
        if (isRunInBackground()) {
            requestManager.getLooperHandler().post(runnable);
        } else {
            runnable.run();
        }
    }

    public void execute(final NoteManager noteManager) throws Exception {
    }

    /**
     * drawToView Instantly when finish request,reused isRender Flag
     * @param noteManager
     */
    public void postExecute(final NoteManager noteManager){
        if (getException() != null) {
            getException().printStackTrace();
        }

        if (isRenderToScreen()) {
            noteManager.enableScreenPost(true);
            noteManager.renderToSurfaceView();
        }
        benchmarkEnd();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (getCallback() != null) {
                    getCallback().done(AsyncBaseNoteRequest.this, getException());
                }
                if (isResumeInputProcessor()) {
                    noteManager.resumeDrawing();
                }
                noteManager.getRequestManager().releaseWakeLock();
            }};

        if (isRunInBackground()) {
            noteManager.getRequestManager().getLooperHandler().post(runnable);
        } else {
            runnable.run();
        }
    }

    public final ShapeDataInfo getShapeDataInfo() {
        if (shapeDataInfo == null) {
            shapeDataInfo = new ShapeDataInfo();
        }
        return shapeDataInfo;
    }

    public void updateShapeDataInfo(final AsyncNoteViewHelper parent) {
        final ShapeDataInfo shapeDataInfo = getShapeDataInfo();
        parent.updateShapeDataInfo(getContext(), shapeDataInfo);
    }

    public void ensureDocumentOpened(final AsyncNoteViewHelper parent) {
        if (!parent.getNoteDocument().isOpen()) {
            parent.getNoteDocument().open(getContext(),
                    getDocUniqueId(),
                    getParentLibraryId());
        }
    }

    public void syncDrawingArgs(final NoteDrawingArgs args) {
        getShapeDataInfo().getDrawingArgs().copyFrom(args);
    }

    public final NoteDrawingArgs getDrawingArgs() {
        return getShapeDataInfo().getDrawingArgs();
    }

    public void setIdentifier(final String id) {
        identifier = id;
    }

    public String getIdentifier() {
        return identifier;
    }
}
