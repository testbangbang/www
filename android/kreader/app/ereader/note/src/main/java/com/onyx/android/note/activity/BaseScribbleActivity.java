package com.onyx.android.note.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.onyx.android.note.NoteApplication;
import com.onyx.android.note.R;
import com.onyx.android.note.actions.scribble.DocumentAddNewPageAction;
import com.onyx.android.note.actions.scribble.DocumentCreateAction;
import com.onyx.android.note.actions.scribble.DocumentDeletePageAction;
import com.onyx.android.note.actions.scribble.DocumentEditAction;
import com.onyx.android.note.actions.scribble.DocumentFlushAction;
import com.onyx.android.note.actions.scribble.GotoNextPageAction;
import com.onyx.android.note.actions.scribble.GotoPrevPageAction;
import com.onyx.android.note.actions.scribble.RemoveByPointListAction;
import com.onyx.android.note.receiver.DeviceReceiver;
import com.onyx.android.note.utils.Constant;
import com.onyx.android.note.utils.NoteAppConfig;
import com.onyx.android.note.utils.Utils;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;
import com.onyx.android.sdk.scribble.request.ShapeDataInfo;
import com.onyx.android.sdk.scribble.shape.RenderContext;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;
import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;
import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;
import com.onyx.android.sdk.ui.dialog.OnyxCustomDialog;

import java.io.File;
import java.util.List;

/**
 * Created by solskjaer49 on 16/8/3 12:25.
 */

public abstract class BaseScribbleActivity extends OnyxAppCompatActivity implements ScribbleInterface {
    public static final String TAG_NOTE_TITLE = "note_title";
    protected ShapeDataInfo shapeDataInfo = new ShapeDataInfo();
    protected DeviceReceiver deviceReceiver = new DeviceReceiver();
    protected SurfaceHolder.Callback surfaceCallback;
    protected SurfaceView surfaceView;
    private TouchPoint erasePoint = null;
    protected String activityAction;
    protected String noteTitle;
    protected String parentID;
    protected String uniqueID;
    protected Button pageIndicator;
    boolean isSurfaceViewFirstCreated = false;
    protected int currentVisualPageIndex;
    protected int totalPageCount;
    protected boolean isLineLayoutMode = false;
    protected boolean fullUpdate = false;
    protected boolean drawPageDuringErasing = false;

    private enum ActivityState {CREATE, RESUME, PAUSE, DESTROY}
    private ActivityState activityState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setActivityState(ActivityState.CREATE);
        super.onCreate(savedInstanceState);
        registerDeviceReceiver();
        isSurfaceViewFirstCreated = true;
    }

    @Override
    protected void onResume() {
        setActivityState(ActivityState.RESUME);
        super.onResume();
        initSurfaceView();
        //TODO:resume status when activity Resume;
        syncWithCallback(true, !shapeDataInfo.isInUserErasing(), null);
    }

    @Override
    protected void onPause() {
        setActivityState(ActivityState.PAUSE);
        super.onPause();
        //TODO:pause drawing when activity Pause;
        syncWithCallback(true, false, null);
    }

    public ActivityState getActivityState() {
        return activityState;
    }

    public void setActivityState(ActivityState activityState) {
        this.activityState = activityState;
    }

    public boolean isActivityRunning() {
        return getActivityState() == ActivityState.CREATE ||
                getActivityState() == ActivityState.RESUME;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_PAGE_DOWN:
                onNextPage();
                return true;
            case KeyEvent.KEYCODE_PAGE_UP:
                onPrevPage();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        setActivityState(ActivityState.DESTROY);
        cleanUpAllPopMenu();
        syncWithCallback(false, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                getNoteViewHelper().quit();
            }
        });
        unregisterDeviceReceiver();
        super.onDestroy();
    }

    @Override
    public void submitRequest(BaseNoteRequest request, BaseCallback callback) {
        getNoteViewHelper().submit(this, request, callback);
    }

    @Override
    public void submitRequestWithIdentifier(String identifier, BaseNoteRequest request, BaseCallback callback) {
        getNoteViewHelper().submitRequestWithIdentifier(this, identifier, request, callback);
    }

    @Override
    public void onRequestFinished(final BaseNoteRequest request, boolean updatePage) {
        updateDataInfo(request);
        if (request.isAbort()) {
            return;
        }
        if (updatePage) {
            drawPage();
        }
    }

    protected void updateDataInfo(final BaseNoteRequest request) {
        shapeDataInfo = request.getShapeDataInfo();
        currentVisualPageIndex = shapeDataInfo.getCurrentPageIndex() + 1;
        totalPageCount = shapeDataInfo.getPageCount();
        if (pageIndicator != null) {
            pageIndicator.setText(currentVisualPageIndex + File.separator + totalPageCount);
        }
    }

    protected NoteViewHelper getNoteViewHelper() {
        return NoteApplication.getNoteViewHelper();
    }

    protected void showNoteNameIllegal() {
        final OnyxAlertDialog illegalDialog = new OnyxAlertDialog();
        OnyxAlertDialog.Params params = new OnyxAlertDialog.Params().setTittleString(getString(R.string.noti))
                .setAlertMsgString(getString(R.string.note_name_already_exist))
                .setEnableNegativeButton(false).setCanceledOnTouchOutside(false)
                .setPositiveAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        illegalDialog.dismiss();
                        syncWithCallback(true, true, null);
                    }
                });
        if (NoteAppConfig.sharedInstance(this).useMXUIStyle()) {
            params.setCustomLayoutResID(R.layout.mx_custom_alert_dialog);
        }
        illegalDialog.setParams(params);
        illegalDialog.show(getFragmentManager(), "illegalDialog");
    }

    protected void syncWithCallback(boolean render,
                                    boolean resume,
                                    final BaseCallback callback) {
        final List<Shape> stash = getNoteViewHelper().detachStash();
        if (isLineLayoutMode()) {
            stash.clear();
        }
        final DocumentFlushAction<BaseScribbleActivity> action = new DocumentFlushAction<>(stash,
                render,
                resume,
                shapeDataInfo.getDrawingArgs());
        action.execute(this, callback);
    }

    protected void registerDeviceReceiver() {
        deviceReceiver.setSystemUIChangeListener(new DeviceReceiver.SystemUIChangeListener() {
            @Override
            public void onSystemUIChanged(String type, boolean open) {
                if (open) {
                    onSystemUIOpened();
                } else {
                    onSystemUIClosed();
                }
            }

            @Override
            public void onHomeClicked() {
                getNoteViewHelper().enableScreenPost(true);
                finish();
            }

            @Override
            public void onScreenShot(Intent intent, boolean end) {
                if (end) {
                    onScreenShotEnd(intent.getBooleanExtra(Constant.RELOAD_DOCUMENT_TAG, false));
                } else {
                    onScreenShotStart();
                }
            }
        });
        deviceReceiver.registerReceiver(this);
    }

    protected void onScreenShotStart(){
        onSystemUIOpened();
    }

    protected void onScreenShotEnd(boolean reloadDocument){
        onSystemUIClosed();
    }

    protected void unregisterDeviceReceiver() {
        deviceReceiver.unregisterReceiver(this);
    }

    protected void onSystemUIOpened() {
        if (isActivityRunning()) {
            syncWithCallback(true, false, null);
        }
    }

    protected void onSystemUIClosed() {
        if (isActivityRunning()) {
            syncWithCallback(true, !shapeDataInfo.isInUserErasing(), null);
        }
    }

    protected void initSurfaceView() {
        surfaceView = (SurfaceView) findViewById(R.id.note_view);
        surfaceView.getHolder().addCallback(surfaceCallback());
    }

    protected SurfaceHolder.Callback surfaceCallback() {
        if (surfaceCallback == null) {
            surfaceCallback = new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {
                    clearSurfaceView();
                    getNoteViewHelper().setView(BaseScribbleActivity.this, surfaceView, inputCallback());
                    handleActivityIntent(getIntent());
                    isSurfaceViewFirstCreated = false;
                }

                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                    surfaceHolder.removeCallback(surfaceCallback);
                    surfaceCallback = null;
                }
            };
        }
        return surfaceCallback;
    }

    protected void handleActivityIntent(final Intent intent) {
        if (!isSurfaceViewFirstCreated) {
            return;
        }
        if (!intent.hasExtra(Utils.ACTION_TYPE)) {
            handleDocumentCreate(ShapeUtils.generateUniqueId(), null);
            return;
        }
        activityAction = intent.getStringExtra(Utils.ACTION_TYPE);
        noteTitle = intent.getStringExtra(TAG_NOTE_TITLE);
        parentID = intent.getStringExtra(Utils.PARENT_LIBRARY_ID);
        uniqueID = intent.getStringExtra(Utils.DOCUMENT_ID);
        if (Utils.ACTION_CREATE.equals(activityAction)) {
            handleDocumentCreate(uniqueID,
                    parentID);
        } else if (Utils.ACTION_EDIT.equals(activityAction)) {
            handleDocumentEdit(uniqueID,
                    parentID);
        }
    }

    protected void handleDocumentCreate(final String uniqueId, final String parentId) {
        final DocumentCreateAction<BaseScribbleActivity> action = new DocumentCreateAction<>(uniqueId, parentId);
        action.execute(this);
    }

    protected void handleDocumentEdit(final String uniqueId, final String parentId) {
        final DocumentEditAction<BaseScribbleActivity> action = new DocumentEditAction<>(uniqueId, parentId);
        action.execute(this);
    }

    protected NoteViewHelper.InputCallback inputCallback() {
        return new NoteViewHelper.InputCallback() {
            @Override
            public void onBeginRawData() {
                onStartDrawing();
            }

            @Override
            public void onRawTouchPointListReceived(final Shape shape, TouchPointList pointList) {
                onNewTouchPointListReceived(shape, pointList);
                triggerLineLayoutMode(isLineLayoutMode());
            }

            @Override
            public void onBeginErasing() {
                BaseScribbleActivity.this.onBeginErasing();
            }

            @Override
            public void onErasing(final MotionEvent touchPoint) {
                BaseScribbleActivity.this.onErasing(touchPoint);
            }

            @Override
            public void onEraseTouchPointListReceived(TouchPointList pointList) {
                onFinishErasing(pointList);
            }

            @Override
            public void onDrawingTouchDown(final MotionEvent motionEvent, final Shape shape) {
                if (!shape.supportDFB()) {
                    drawPage();
                }
            }

            @Override
            public void onDrawingTouchMove(final MotionEvent motionEvent, final Shape shape, boolean last) {
                if (last && !shape.supportDFB()) {
                    drawPage();
                }
            }

            @Override
            public void onDrawingTouchUp(final MotionEvent motionEvent, final Shape shape) {
                if (!shape.supportDFB()) {
                    drawPage();
                }
                triggerLineLayoutMode(isLineLayoutMode());
            }

        };
    }

    protected void triggerLineLayoutMode(boolean isSpanMode) {

    }

    protected void onNewTouchPointListReceived(final Shape shape, TouchPointList pointList) {
        //final AddShapeInBackgroundAction<ScribbleActivity> action = new AddShapeInBackgroundAction<>(shape);
        //action.execute(this, null);
    }

    protected void onBeginErasing() {
        syncWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                erasePoint = new TouchPoint();
            }
        });
    }

    protected void onErasing(final MotionEvent touchPoint) {
        if (erasePoint == null) {
            return;
        }
        erasePoint.x = touchPoint.getX();
        erasePoint.y = touchPoint.getY();
        if (drawPageDuringErasing) {
            drawPage();
        }
    }

    protected void onFinishErasing(TouchPointList pointList) {
        erasePoint = null;
        RemoveByPointListAction<BaseScribbleActivity> removeByPointListAction = new
                RemoveByPointListAction<>(pointList);
        removeByPointListAction.execute(this, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                drawPage();
            }
        });
    }

    private void drawContent(final Canvas canvas, final Paint paint) {
        Bitmap bitmap = getNoteViewHelper().getViewBitmap();
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, 0, 0, paint);
        }
    }

    private void drawStashShape(final Canvas canvas, final Paint paint) {
        final RenderContext renderContext = RenderContext.create(canvas, paint, null);
        final List<Shape> stash = getNoteViewHelper().getDirtyStash();
        for (Shape shape : stash) {
            shape.render(renderContext);
        }
    }

    private void drawErasingIndicator(final Canvas canvas, final Paint paint) {
        if (erasePoint == null || erasePoint.getX() <= 0 || erasePoint.getY() <= 0) {
            return;
        }

        float x = erasePoint.getX();
        float y = erasePoint.getY();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2.0f);
        canvas.drawCircle(x, y, shapeDataInfo.getEraserRadius(), paint);
    }

    public void drawPage() {
        Rect rect = getViewportSize();
        Canvas canvas = beforeDraw(rect);
        if (canvas == null) {
            resetFullUpdate();
            return;
        }

        Paint paint = new Paint();
        cleanup(canvas, paint, rect);
        drawContent(canvas, paint);
        drawStashShape(canvas, paint);
        afterDraw(canvas);
    }

    protected void clearSurfaceView() {
        Rect rect = getViewportSize();
        Canvas canvas = beforeDraw(rect);
        if (canvas == null) {
            return;
        }

        Paint paint = new Paint();
        cleanup(canvas, paint, rect);
        afterDraw(canvas);
    }

    protected Rect getViewportSize() {
        return new Rect(0, 0, surfaceView.getWidth(), surfaceView.getHeight());
    }

    protected void cleanup(final Canvas canvas, final Paint paint, final Rect rect) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawRect(rect, paint);
    }

    protected Canvas beforeDraw(final Rect rect) {
        if (isFullUpdate()) {
            EpdController.setViewDefaultUpdateMode(surfaceView, UpdateMode.GC);
        } else {
            EpdController.resetUpdateMode(surfaceView);
        }
        return surfaceView.getHolder().lockCanvas(rect);
    }

    protected void afterDraw(final Canvas canvas) {
        surfaceView.getHolder().unlockCanvasAndPost(canvas);
        resetFullUpdate();
    }

    protected void onNextPage() {
        syncWithCallback(false, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                final GotoNextPageAction<BaseScribbleActivity> action = new GotoNextPageAction<>();
                action.execute(BaseScribbleActivity.this, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        onRequestFinished((BaseNoteRequest) request, true);
                        reloadLineLayoutData();
                    }
                });
            }
        });
    }

    protected void onPrevPage() {
        syncWithCallback(false, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                final GotoPrevPageAction<BaseScribbleActivity> action = new GotoPrevPageAction<>();
                action.execute(BaseScribbleActivity.this, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        onRequestFinished((BaseNoteRequest) request, true);
                        reloadLineLayoutData();
                    }
                });
            }
        });
    }

    protected void onAddNewPage() {
        syncWithCallback(false, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                final DocumentAddNewPageAction<BaseScribbleActivity> action = new DocumentAddNewPageAction<>(-1);
                action.execute(BaseScribbleActivity.this, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        onRequestFinished((BaseNoteRequest) request, true);
                        reloadLineLayoutData();
                    }
                });
            }
        });
    }

    protected void onDeletePage() {
        syncWithCallback(false, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                deletePage();
            }
        });
    }

    private void deletePage() {
        OnyxCustomDialog dialog = OnyxCustomDialog.getConfirmDialog(this, getString(R.string.ask_for_delete_page), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final DocumentDeletePageAction<BaseScribbleActivity> action = new DocumentDeletePageAction<>();
                action.execute(BaseScribbleActivity.this, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        onRequestFinished((BaseNoteRequest) request, true);
                        reloadLineLayoutData();
                    }
                });
            }
        }, null);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                syncWithCallback(false, true, null);
            }
        });
        dialog.show();
    }

    protected void reloadLineLayoutData() {

    }

    protected void onStartDrawing() {

    }

    protected void setCurrentShapeType(int type) {
        shapeDataInfo.setCurrentShapeType(type);
    }

    protected void setBackgroundType(int type) {
        shapeDataInfo.setBackground(type);
    }

    protected int getBackgroundType() {
        return shapeDataInfo.getBackground();
    }

    protected void setStrokeWidth(float width) {
        shapeDataInfo.setStrokeWidth(width);
    }

    protected void setStrokeColor(int color) {
        shapeDataInfo.setStrokeColor(color);
    }

    protected int getCurrentShapeColor() {
        return shapeDataInfo.getStrokeColor();
    }

    protected abstract void cleanUpAllPopMenu();

    public boolean isLineLayoutMode() {
        return isLineLayoutMode;
    }

    public void setLineLayoutMode(boolean lineLayoutMode) {
        isLineLayoutMode = lineLayoutMode;
        getNoteViewHelper().setLineLayoutMode(isLineLayoutMode);
    }

    public void toggleLineLayoutMode() {
        isLineLayoutMode = !isLineLayoutMode;
        getNoteViewHelper().setLineLayoutMode(isLineLayoutMode);
    }

    public SurfaceView getSurfaceView() {
        return surfaceView;
    }

    public boolean isFullUpdate() {
        return fullUpdate;
    }

    public void setFullUpdate(boolean fullUpdate) {
        this.fullUpdate = fullUpdate;
    }

    public void resetFullUpdate() {
        this.fullUpdate = false;
    }
}