package com.onyx.kreader.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.ActionBarActivity;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.ui.data.ReaderStatusInfo;
import com.onyx.android.sdk.ui.view.ReaderStatusBar;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.BuildConfig;
import com.onyx.kreader.R;
import com.onyx.kreader.dataprovider.LegacySdkDataUtils;
import com.onyx.kreader.device.ReaderDeviceManager;
import com.onyx.kreader.note.actions.FlushNoteAction;
import com.onyx.kreader.note.request.ReaderNoteRenderRequest;
import com.onyx.kreader.ui.actions.*;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.data.SingletonSharedPreference;
import com.onyx.kreader.ui.dialog.DialogScreenRefresh;
import com.onyx.kreader.ui.events.*;
import com.onyx.kreader.ui.gesture.MyOnGestureListener;
import com.onyx.kreader.ui.gesture.MyScaleGestureListener;
import com.onyx.kreader.ui.handler.HandlerManager;
import com.onyx.kreader.ui.settings.MainSettingsActivity;
import com.onyx.kreader.utils.DeviceUtils;
import com.onyx.kreader.utils.TreeObserverUtils;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Joy on 2016/4/14.
 */
public class ReaderActivity extends ActionBarActivity {
    private static final String DOCUMENT_PATH_TAG = "document";

    private PowerManager.WakeLock startupWakeLock;
    private SurfaceView surfaceView;
    private RelativeLayout mainView;
    private SurfaceHolder.Callback surfaceHolderCallback;
    private SurfaceHolder holder;
    private ReaderStatusBar statusBar;

    private ReaderDataHolder readerDataHolder;
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleDetector;
    private final ReaderPainter readerPainter = new ReaderPainter();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        acquireStartupWakeLock();
        setContentView(R.layout.activity_reader);
        initComponents();
    }

    @Override
    protected void onResume() {
        checkForNewConfiguration();
        super.onResume();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        ViewTreeObserver observer = surfaceView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                removeGlobalOnLayoutListener(this);
                onSurfaceViewSizeChanged();
                if (!getReaderDataHolder().isDocumentOpened()) {
                    handleActivityIntent();
                }
            }
        });

        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void startActivity(Intent intent) {
        // check if search intent
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            intent.putExtra(DOCUMENT_PATH_TAG, readerDataHolder.getDocumentPath());
        }

        super.startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        try {
            setIntent(intent);
            handleActivityIntent();
        } catch (java.lang.Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        ReaderActivity.super.onDestroy();
    }

    private void resetMenus() {
        hideAllPopupMenu(null);
        ShowReaderMenuAction.resetReaderMenu(getReaderDataHolder());
        ShowSearchMenuAction.resetSearchMenu();
        ShowTextSelectionMenuAction.resetSelectionMenu();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return processKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return processKeyUp(keyCode, event);
    }

    private final ReaderDataHolder getReaderDataHolder(){
        return readerDataHolder;
    }

    public final HandlerManager getHandlerManager() {
        return readerDataHolder.getHandlerManager();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initComponents() {
        initStatusBar();
        initReaderDataHolder();
        initSurfaceView();
    }

    private void initStatusBar() {
        statusBar = (ReaderStatusBar)findViewById(R.id.status_bar);
        statusBar.setCallback(new ReaderStatusBar.Callback() {
            @Override
            public void onGotoPage() {
                new ShowQuickPreviewAction().execute(readerDataHolder, null);
            }
        });
        reconfigStatusBar();
    }

    private void reconfigStatusBar() {
        statusBar.reConfigure(SingletonSharedPreference.getBooleanByStringID(this, R.string.settings_battery_percentage_show_key, false),
                SingletonSharedPreference.getBooleanByStringID(this, R.string.settings_time_show_key, false),
                SingletonSharedPreference.getBooleanByStringID(this, R.string.settings_time_show_format_key, false),
                SingletonSharedPreference.getBooleanByStringID(this, R.string.settings_battery_graphic_show_key, false));

        if (!SingletonSharedPreference.isReaderStatusBarEnabled(this)){
            statusBar.setVisibility(View.GONE);
        } else {
            statusBar.setVisibility(View.VISIBLE);
        }
        statusBar.reConfigure(SingletonSharedPreference.isStatusBarShowBatteryPercentage(this),
                SingletonSharedPreference.isStatusBarTimeShow(this),
                SingletonSharedPreference.isStatusBarTime24HourFormat(this),
                SingletonSharedPreference.isStatusBarShowBatteryGraphical(this));
    }

    private void initSurfaceView() {
        mainView = (RelativeLayout) findViewById(R.id.main_view);
        surfaceView = (SurfaceView) this.findViewById(R.id.surfaceView);
        surfaceHolderCallback = new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                clearCanvas(holder);
                if (!readerDataHolder.isDocumentOpened()) {
                    return;
                }
                if (surfaceView.getWidth() != readerDataHolder.getDisplayWidth() ||
                    surfaceView.getHeight() != readerDataHolder.getDisplayHeight()) {
                    onSurfaceViewSizeChanged();
                } else {
                    readerDataHolder.redrawPage();
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        };

        surfaceView.getHolder().addCallback(surfaceHolderCallback);
        holder = surfaceView.getHolder();
        gestureDetector = new GestureDetector(this, new MyOnGestureListener(getReaderDataHolder()));
        scaleDetector = new ScaleGestureDetector(this, new MyScaleGestureListener(getReaderDataHolder()));
        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                getHandlerManager().setTouchStartEvent(event);
                scaleDetector.onTouchEvent(event);
                gestureDetector.onTouchEvent(event);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    getHandlerManager().onActionUp(getReaderDataHolder(), event);
                    getHandlerManager().resetTouchStartPosition();
                }

                getHandlerManager().onTouchEvent(getReaderDataHolder(), event);
                return true;
            }
        });

        surfaceView.setFocusable(true);
        surfaceView.setFocusableInTouchMode(true);
        surfaceView.requestFocusFromTouch();

        // make sure we openFileFromIntent the doc after surface view is layouted correctly.
        surfaceView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                removeGlobalOnLayoutListener(this);
                onSurfaceViewSizeChanged();
                handleActivityIntent();
            }
        });
    }

    private void removeGlobalOnLayoutListener(ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (Build.VERSION.SDK_INT < 16) {
            TreeObserverUtils.removeLayoutListenerPre16(surfaceView.getViewTreeObserver(), listener);
        } else {
            TreeObserverUtils.removeLayoutListenerPost16(surfaceView.getViewTreeObserver(), listener);
        }
    }

    private void initReaderDataHolder() {
        readerDataHolder = new ReaderDataHolder(this);
        readerDataHolder.getEventBus().register(this);
        getHandlerManager().setEnable(false);
    }

    private void checkForNewConfiguration() {
        setFullScreen(!SingletonSharedPreference.isSystemStatusBarEnabled(this));
        reconfigStatusBar();
        checkSurfaceViewSize();
    }

    private void checkSurfaceViewSize() {
        if (!readerDataHolder.isDocumentOpened()) {
            return;
        }

        surfaceView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                surfaceView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                if (surfaceView.getWidth() != readerDataHolder.getDisplayWidth() ||
                        surfaceView.getHeight() != readerDataHolder.getDisplayHeight()) {
                    onSurfaceViewSizeChanged();
                }
            }
        });
    }

    @Subscribe
    public void onChangeEpdUpdateMode(final ChangeEpdUpdateModeEvent event) {
        ReaderDeviceManager.setUpdateMode(surfaceView, event.getTargetMode());
    }

    @Subscribe
    public void onResetEpdUpdateMode(final ResetEpdUpdateModeEvent event) {
        ReaderDeviceManager.resetUpdateMode(surfaceView);
    }

    @Subscribe
    public void onRequestFinished(final RequestFinishEvent event) {
        if (event != null && event.isApplyGCIntervalUpdate()) {
            ReaderDeviceManager.applyWithGCInterval(surfaceView, readerDataHolder.getReaderViewInfo().isTextPages());
        }
        drawPage(getReaderDataHolder().getReader().getViewportBitmap().getBitmap());
        updateStatusBar();
        if (event != null && event.isRenderShapeData()) {
            renderShapeDataInBackground();
        }
    }

    @Subscribe
    public void onShapeDrawing(final ShapeDrawingEvent event) {
        drawPage(getReaderDataHolder().getReader().getViewportBitmap().getBitmap());
    }

    @Subscribe
    public void onShowReaderSettings(final ShowReaderSettingsEvent event) {
        startActivity(new Intent(this, MainSettingsActivity.class));
    }

    private void clearCanvas(SurfaceHolder holder) {
        if (holder == null) {
            return;
        }
        Canvas canvas = holder.lockCanvas();
        if (canvas != null) {
            canvas.drawColor(Color.WHITE);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    private void acquireStartupWakeLock() {
        if (startupWakeLock == null) {
            startupWakeLock = Device.currentDevice().newWakeLock(this, ReaderActivity.class.getSimpleName());
        }
        if (startupWakeLock != null) {
            startupWakeLock.acquire();
        }
    }

    private void releaseStartupWakeLock() {
        if (startupWakeLock != null && startupWakeLock.isHeld()) {
            startupWakeLock.release();
            startupWakeLock = null;
        }
    }

    private boolean handleActivityIntent() {
        try {
            String action = getIntent().getAction();
            if (StringUtils.isNullOrEmpty(action)) {
                openBuiltInDoc();
            } else if (action.equals(Intent.ACTION_MAIN)) {
                quitApplication(null);
            } else if (action.equals(Intent.ACTION_VIEW)) {
                openFileFromIntent();
            }
            return true;
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void openFileFromIntent() {
        Uri uri = getIntent().getData();
        if (uri == null) {
            return;
        }

        final String path = FileUtils.getRealFilePathFromUri(ReaderActivity.this, uri);
        if (isFileAlreadyOpened(path)) {
            return;
        }

        final OpenDocumentAction action = new OpenDocumentAction(this, path);
        action.execute(getReaderDataHolder(), null);
        releaseStartupWakeLock();
    }

    private boolean isFileAlreadyOpened(final String path) {
        if (getReaderDataHolder() == null || StringUtils.isBlank(getReaderDataHolder().getDocumentPath())) {
            return false;
        }
        return path.equals(getReaderDataHolder().getDocumentPath());
    }

    private void onSurfaceViewSizeChanged() {
        getReaderDataHolder().setDisplaySize(surfaceView.getWidth(), surfaceView.getHeight());
        getReaderDataHolder().getNoteManager().updateSurfaceView(this, surfaceView);
        if (getReaderDataHolder().isDocumentOpened()) {
            new ChangeViewConfigAction().execute(getReaderDataHolder(), null);
        }
    }

    @Subscribe
    public void onBeforeDocumentOpen(final BeforeDocumentOpenEvent event) {
        EpdController.enablePost(surfaceView, 1);
        resetMenus();
    }

    @Subscribe
    public void onBeforeDocumentClose(final BeforeDocumentCloseEvent event) {
        EpdController.enablePost(surfaceView, 1);
        resetMenus();
    }

    @Subscribe
    public void onDocumentOpened(final DocumentOpenEvent event) {
        ReaderDeviceManager.prepareInitialUpdate(LegacySdkDataUtils.getScreenUpdateGCInterval(this,
                DialogScreenRefresh.DEFAULT_INTERVAL_COUNT));
    }

    @Subscribe
    public void onChangeOrientation(final ChangeOrientationEvent event) {
        setRequestedOrientation(event.getOrientation());
    }

    public void backward() {
        final BackwardAction backwardAction = new BackwardAction();
        backwardAction.execute(getReaderDataHolder(), null);
    }

    public void forward() {
        final ForwardAction forwardAction = new ForwardAction();
        forwardAction.execute(getReaderDataHolder(), null);
    }

    private void drawPage(final Bitmap pageBitmap) {
        Canvas canvas = holder.lockCanvas(new Rect(surfaceView.getLeft(), surfaceView.getTop(),
                surfaceView.getRight(), surfaceView.getBottom()));
        if (canvas == null) {
            return;
        }
        readerPainter.drawPage(this,
                canvas,
                pageBitmap,
                getReaderDataHolder().getReaderUserDataInfo(),
                getReaderDataHolder().getReaderViewInfo(),
                getReaderDataHolder().getSelectionManager(),
                getReaderDataHolder().getNoteManager(),
                getReaderDataHolder().getNoteDataInfo());
        holder.unlockCanvasAndPost(canvas);
    }

    private void renderShapeDataInBackground() {
        final ReaderNoteRenderRequest renderRequest = new ReaderNoteRenderRequest(
                getReaderDataHolder().getReader().getDocumentMd5(),
                getReaderDataHolder().getReaderViewInfo().getVisiblePages(),
                getReaderDataHolder().getDisplayRect(),
                true);
        getReaderDataHolder().getNoteManager().submit(this, renderRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null || request.isAbort()) {
                    return;
                }
                getReaderDataHolder().saveShapeDataInfo(renderRequest);
                onRequestFinished(RequestFinishEvent.shapeReadyEvent());
            }
        });
    }

    public SurfaceHolder getHolder() {
        return holder;
    }

    @Subscribe
    public void quitApplication(final QuitEvent event) {
        final CloseActionChain closeAction = new CloseActionChain();
        closeAction.execute(getReaderDataHolder(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                readerDataHolder.getEventBus().unregister(this);
                releaseStartupWakeLock();
                finish();
            }
        });
    }

    private void openBuiltInDoc() {
        if (!BuildConfig.DEBUG) {
            return;
        }
        final String path = "/mnt/sdcard/Books/a.pdf";
        final OpenDocumentAction action = new OpenDocumentAction(this, path);
        action.execute(getReaderDataHolder(), null);
        releaseStartupWakeLock();
    }

    private boolean hasPopupWindow() {
        return ShowReaderMenuAction.isReaderMenuShown();
    }

    @Subscribe
    private void hideAllPopupMenu(final ClosePopupEvent event) {
        ShowReaderMenuAction.hideReaderMenu();
        ShowTextSelectionMenuAction.hideTextSelectionPopupMenu();
        getReaderDataHolder().closeActiveDialogs();
    }

    protected boolean askForClose() {
        return false;
    }

    private boolean processKeyDown(int keyCode, KeyEvent event) {
        return getHandlerManager().onKeyDown(getReaderDataHolder(), keyCode, event) || super.onKeyDown(keyCode, event);
    }

    private boolean processKeyUp(int keyCode, KeyEvent event) {
        return getHandlerManager().onKeyUp(getReaderDataHolder(), keyCode, event) || super.onKeyUp(keyCode, event);
    }

    private void updateStatusBar() {
        PageInfo pageInfo = getReaderDataHolder().getFirstPageInfo();
        Rect pageRect = new Rect();
        Rect displayRect = new Rect();
        pageInfo.getPositionRect().round(pageRect);
        translateDisplayRectToViewportRect(pageInfo.getDisplayRect()).round(displayRect);
        int current = getReaderDataHolder().getCurrentPage() + 1;
        int total = getReaderDataHolder().getPageCount();
        String title = getReaderDataHolder().getBookName();
        if (SingletonSharedPreference.isShowDocTitleInStatusBar(this)) {
            if (StringUtils.isNotBlank(getReaderDataHolder().getBookTitle())) {
                title = getReaderDataHolder().getBookTitle();
            }
        }
        statusBar.updateStatusBar(new ReaderStatusInfo(pageRect, displayRect,
                current, total, 0, title));
    }

    private RectF translateDisplayRectToViewportRect(RectF displayRect) {
        RectF rect = new RectF(displayRect);
        rect.intersect(0, 0, getReaderDataHolder().getDisplayWidth(), getReaderDataHolder().getDisplayHeight());
        rect.offset(-displayRect.left, -displayRect.top);
        return rect;
    }

    public void setFullScreen(boolean fullScreen) {
        DeviceUtils.setFullScreen(this, fullScreen);
    }

    public SurfaceView getSurfaceView() {
        return surfaceView;
    }

    public RelativeLayout getMainView() {
        return mainView;
    }

    public ReaderStatusBar getStatusBar() {
        return statusBar;
    }
}