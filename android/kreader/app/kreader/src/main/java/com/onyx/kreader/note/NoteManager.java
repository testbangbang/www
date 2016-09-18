package com.onyx.kreader.note;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.SurfaceView;
import android.view.ViewTreeObserver;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.RequestManager;
import com.onyx.android.sdk.data.ReaderBitmapImpl;
import com.onyx.android.sdk.scribble.data.NotePage;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.math.OnyxMatrix;
import com.onyx.android.sdk.scribble.request.ShapeDataInfo;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.touch.RawInputProcessor;
import com.onyx.android.sdk.scribble.utils.DeviceConfig;
import com.onyx.kreader.common.Debug;
import com.onyx.kreader.note.data.ReaderNoteDataInfo;
import com.onyx.kreader.note.data.ReaderNoteDocument;
import com.onyx.kreader.note.request.ReaderBaseNoteRequest;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by zhuzeng on 9/2/16.
 */
public class NoteManager {

    private RequestManager requestManager = new RequestManager(Thread.NORM_PRIORITY);
    private RawInputProcessor rawInputProcessor = new RawInputProcessor();
    private ReaderNoteDocument noteDocument = new ReaderNoteDocument();
    private ReaderBitmapImpl renderBitmapWrapper = new ReaderBitmapImpl();
    private ReaderBitmapImpl viewBitmapWrapper = new ReaderBitmapImpl();
    private Rect limitRect = null;
    private volatile SurfaceView surfaceView;
    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener;
    private List<Shape> dirtyStash = new ArrayList<>();
    private TouchPointList erasePoints;
    private DeviceConfig deviceConfig;
    private Shape currentShape = null;
    private boolean shortcutErasing = false;
    private OnyxMatrix viewToEpdMatrix = null;
    private int viewPosition[] = {0, 0};

    public final RequestManager getRequestManager() {
        return requestManager;
    }

    public final ReaderNoteDocument getNoteDocument() {
        return noteDocument;
    }

    public void updateShapeDataInfo(final Context context, final ReaderNoteDataInfo shapeDataInfo) {
        shapeDataInfo.updateDrawingArgs(getNoteDocument().getNoteDrawingArgs());
//        shapeDataInfo.setCanRedoShape(getNoteDocument().getCurrentPage(context).canRedo());
//        shapeDataInfo.setCanUndoShape(getNoteDocument().getCurrentPage(context).canUndo());
        shapeDataInfo.setDocumentUniqueId(getNoteDocument().getDocumentUniqueId());
    }

    public void enableScreenPost(boolean enable) {
        if (surfaceView != null) {
            EpdController.enablePost(surfaceView, enable ? 1 : 0);
        }
    }

    public Bitmap updateRenderBitmap(final Rect viewportSize) {
        renderBitmapWrapper.update(viewportSize.width(), viewportSize.height(), Bitmap.Config.ARGB_8888);
        return renderBitmapWrapper.getBitmap();
    }

    public Bitmap getRenderBitmap() {
        return renderBitmapWrapper.getBitmap();
    }

    // copy from render bitmap to view bitmap.
    public void copyBitmap() {
        if (renderBitmapWrapper == null) {
            return;
        }
        final Bitmap bitmap = renderBitmapWrapper.getBitmap();
        if (bitmap == null) {
            return;
        }
        viewBitmapWrapper.copyFrom(bitmap);
    }

    public Bitmap getViewBitmap() {
        if (viewBitmapWrapper == null) {
            return null;
        }
        return viewBitmapWrapper.getBitmap();
    }

    public void submit(final Context context, final ReaderBaseNoteRequest request, final BaseCallback callback) {
        beforeSubmit(context, request, callback);
        getRequestManager().submitRequest(context, request, generateRunnable(request), callback);
    }

    public void submitRequestWithIdentifier(final Context context,
                                            final String identifier,
                                            final ReaderBaseNoteRequest request,
                                            final BaseCallback callback) {
        beforeSubmit(context, request, callback);
        getRequestManager().submitRequest(context, identifier, request, generateRunnable(request), callback);
    }

    private void beforeSubmit(final Context context, final ReaderBaseNoteRequest request, final BaseCallback callback) {
        final Rect rect = getViewportSize();
        if (rect != null) {
            request.setViewportSize(rect);
        }
    }

    private final Runnable generateRunnable(final ReaderBaseNoteRequest request) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    request.beforeExecute(NoteManager.this);
                    request.execute(NoteManager.this);
                } catch (java.lang.Exception exception) {
                    Debug.d(exception.toString());
                    request.setException(exception);
                } finally {
                    request.afterExecute(NoteManager.this);
                    getRequestManager().dumpWakelocks();
                    getRequestManager().removeRequest(request);
                }
            }
        };
        return runnable;
    }

    private Rect getViewportSize() {
        if (surfaceView != null) {
            return new Rect(0, 0, surfaceView.getWidth(), surfaceView.getHeight());
        }
        return null;
    }




}
