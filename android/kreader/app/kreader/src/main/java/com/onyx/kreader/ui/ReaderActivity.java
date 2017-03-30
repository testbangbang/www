package com.onyx.kreader.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
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
import android.widget.Toast;

import com.onyx.android.sdk.api.device.FrontLightController;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.reader.common.Debug;
import com.onyx.android.sdk.ui.data.ReaderStatusInfo;
import com.onyx.android.sdk.ui.view.ReaderStatusBar;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.utils.DeviceUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.BuildConfig;
import com.onyx.kreader.R;
import com.onyx.android.sdk.reader.dataprovider.LegacySdkDataUtils;
import com.onyx.kreader.device.ReaderDeviceManager;
import com.onyx.kreader.note.actions.FlushNoteAction;
import com.onyx.kreader.note.actions.RemoveShapesByTouchPointListAction;
import com.onyx.kreader.note.actions.ResumeDrawingAction;
import com.onyx.kreader.note.actions.StopNoteActionChain;
import com.onyx.kreader.note.data.ReaderNoteDataInfo;
import com.onyx.kreader.note.request.ReaderNoteRenderRequest;
import com.onyx.kreader.ui.actions.BackwardAction;
import com.onyx.kreader.ui.actions.ChangeViewConfigAction;
import com.onyx.kreader.ui.actions.CloseActionChain;
import com.onyx.kreader.ui.actions.ForwardAction;
import com.onyx.kreader.ui.actions.OpenDocumentAction;
import com.onyx.kreader.ui.actions.SaveDocumentOptionsAction;
import com.onyx.kreader.ui.actions.ShowQuickPreviewAction;
import com.onyx.kreader.ui.actions.ShowReaderMenuAction;
import com.onyx.kreader.ui.actions.ShowTextSelectionMenuAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.data.SingletonSharedPreference;
import com.onyx.kreader.ui.dialog.DialogScreenRefresh;
import com.onyx.kreader.ui.events.BeforeDocumentCloseEvent;
import com.onyx.kreader.ui.events.BeforeDocumentOpenEvent;
import com.onyx.kreader.ui.events.ChangeEpdUpdateModeEvent;
import com.onyx.kreader.ui.events.ChangeOrientationEvent;
import com.onyx.kreader.ui.events.ClosePopupEvent;
import com.onyx.kreader.ui.events.ConfirmCloseDialogEvent;
import com.onyx.kreader.ui.events.DocumentInitRenderedEvent;
import com.onyx.kreader.ui.events.DocumentOpenEvent;
import com.onyx.kreader.ui.events.HomeClickEvent;
import com.onyx.kreader.ui.events.LayoutChangeEvent;
import com.onyx.kreader.ui.events.PinchZoomEvent;
import com.onyx.kreader.ui.events.QuitEvent;
import com.onyx.kreader.ui.events.RequestFinishEvent;
import com.onyx.kreader.ui.events.ResetEpdUpdateModeEvent;
import com.onyx.kreader.ui.events.ScribbleMenuChangedEvent;
import com.onyx.kreader.ui.events.ShapeAddedEvent;
import com.onyx.kreader.ui.events.ShapeDrawingEvent;
import com.onyx.kreader.ui.events.ShapeErasingEvent;
import com.onyx.kreader.ui.events.ShapeRenderFinishEvent;
import com.onyx.kreader.ui.events.ShortcutDrawingFinishedEvent;
import com.onyx.kreader.ui.events.ShortcutDrawingStartEvent;
import com.onyx.kreader.ui.events.ShortcutErasingEvent;
import com.onyx.kreader.ui.events.ShortcutErasingFinishEvent;
import com.onyx.kreader.ui.events.ShortcutErasingStartEvent;
import com.onyx.kreader.ui.events.ShowReaderSettingsEvent;
import com.onyx.kreader.ui.events.SystemUIChangedEvent;
import com.onyx.kreader.ui.gesture.MyOnGestureListener;
import com.onyx.kreader.ui.gesture.MyScaleGestureListener;
import com.onyx.kreader.ui.handler.HandlerManager;
import com.onyx.kreader.ui.receiver.NetworkConnectChangedReceiver;
import com.onyx.kreader.ui.settings.MainSettingsActivity;
import com.onyx.kreader.ui.view.PinchZoomingPopupMenu;
import com.onyx.kreader.device.DeviceConfig;
import com.onyx.android.sdk.reader.utils.TreeObserverUtils;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

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

    private ReaderDataHolder dataHolder;
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleDetector;
    private NetworkConnectChangedReceiver networkConnectChangedReceiver;
    private final ReaderPainter readerPainter = new ReaderPainter();

    private PinchZoomingPopupMenu pinchZoomingPopupMenu;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        acquireStartupWakeLock();
        setContentView(R.layout.activity_reader);
        initComponents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        afterResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disablePenShortcut();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        final ViewTreeObserver observer = surfaceView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                TreeObserverUtils.removeGlobalOnLayoutListener(surfaceView.getViewTreeObserver(), this);
                if (!getReaderDataHolder().isDocumentOpened()) {
                    openFileFromIntentImpl();
                }
            }
        });

        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void startActivity(Intent intent) {
        // check if search intent
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            intent.putExtra(DOCUMENT_PATH_TAG, getReaderDataHolder().getDocumentPath());
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
        if (networkConnectChangedReceiver !=null) {
            unregisterReceiver(networkConnectChangedReceiver);
        }
        ReaderActivity.super.onDestroy();
        if (getReaderDataHolder().isDocumentOpened()) {
            quitApplication(null);
        }
    }

    private void resetMenus() {
        hideAllPopupMenu(null);
        ShowReaderMenuAction.resetReaderMenu(getReaderDataHolder());
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

    private final ReaderDataHolder getReaderDataHolder() {
        return dataHolder;
    }

    public final HandlerManager getHandlerManager() {
        return getReaderDataHolder().getHandlerManager();
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

    private void beforeSetContentView() {
        boolean fullScreen = !SingletonSharedPreference.isSystemStatusBarEnabled(this) || DeviceConfig.sharedInstance(this).isSupportColor();
        DeviceUtils.setFullScreenOnCreate(this, fullScreen);
    }

    private void initComponents() {
        initStatusBar();
        initReaderDataHolder();
        initSurfaceView();
        initReceiver();
    }
    private void initReceiver() {
        networkConnectChangedReceiver = new NetworkConnectChangedReceiver(new NetworkConnectChangedReceiver.NetworkChangedListener() {
            @Override
            public void onNetworkChanged(boolean connected, int networkType) {
                getReaderDataHolder().onNetworkChanged(connected, networkType);
            }

            @Override
            public void onNoNetwork() {

            }
        });
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkConnectChangedReceiver, filter);
    }

    private void initReaderMenu(){
        ShowReaderMenuAction.initDisableMenus(getReaderDataHolder());
    }

    private void initStatusBar() {
        statusBar = (ReaderStatusBar) findViewById(R.id.status_bar);
        statusBar.setCallback(new ReaderStatusBar.Callback() {
            @Override
            public void onGotoPage() {
                new ShowQuickPreviewAction(getReaderDataHolder()).execute(getReaderDataHolder(), null);
            }
        });
        reconfigStatusBar();
    }

    private void reconfigStatusBar() {
        statusBar.reConfigure(SingletonSharedPreference.getBooleanByStringID(this, R.string.settings_battery_percentage_show_key, false),
                SingletonSharedPreference.getBooleanByStringID(this, R.string.settings_time_show_key, false),
                SingletonSharedPreference.getBooleanByStringID(this, R.string.settings_time_show_format_key, false),
                SingletonSharedPreference.getBooleanByStringID(this, R.string.settings_battery_graphic_show_key, false));

        if (!SingletonSharedPreference.isReaderStatusBarEnabled(this)) {
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
                if (!getReaderDataHolder().isDocumentOpened()) {
                    getReaderDataHolder().setDisplaySize(surfaceView.getWidth(), surfaceView.getHeight());
                    return;
                }
                if (!getReaderDataHolder().isDocumentInitRendered()) {
                    return;
                }
                if (surfaceView.getWidth() == getReaderDataHolder().getDisplayWidth() &&
                    surfaceView.getHeight() == getReaderDataHolder().getDisplayHeight()) {
                    getReaderDataHolder().redrawPage();
                } else {
                    onSurfaceViewSizeChanged();
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
        final ViewTreeObserver observer = surfaceView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                TreeObserverUtils.removeGlobalOnLayoutListener(surfaceView.getViewTreeObserver(), this);
                handleActivityIntent();
            }
        });
    }


    private void initReaderDataHolder() {
        dataHolder = new ReaderDataHolder(this);
        dataHolder.getEventBus().register(this);
        getHandlerManager().setEnable(false);
    }

    private void afterResume() {
        syncSystemStatusBar();
        syncReaderPainter();
        reconfigStatusBar();
        enablePenShortcut();
    }

    private void enablePenShortcut() {
        getReaderDataHolder().enablePenShortcut();
    }

    private void disablePenShortcut() {
        getReaderDataHolder().disablePenShortcut();
    }

    private void syncReaderPainter() {
        readerPainter.setAnnotationHighlightStyle(SingletonSharedPreference.getAnnotationHighlightStyle(this));
    }

    private void syncSystemStatusBar() {
        boolean fullScreen = !SingletonSharedPreference.isSystemStatusBarEnabled(this) || DeviceConfig.sharedInstance(this).isSupportColor();
        setFullScreen(fullScreen);
    }

    @Subscribe
    public void onLayoutChanged(final LayoutChangeEvent event) {
        updateNoteHostView();
        getReaderDataHolder().updateRawEventProcessor();
    }

    @Subscribe
    public void onChangeEpdUpdateMode(final ChangeEpdUpdateModeEvent event) {
        ReaderDeviceManager.setUpdateMode(surfaceView, event.getTargetMode());
    }

    @Subscribe
    public void onResetEpdUpdateMode(final ResetEpdUpdateModeEvent event) {
        ReaderDeviceManager.resetUpdateMode(surfaceView);
        ReaderDeviceManager.resetUpdateMode(getStatusBar());
    }

    @Subscribe
    public void onRequestFinished(final RequestFinishEvent event) {
        if (!verifyReader()) {
            return;
        }
        prepareUpdateMode(event);

        if (event != null && !event.isWaitForShapeData()) {
            beforeDrawPage();
            drawPage(getReaderDataHolder().getReader().getViewportBitmap().getBitmap());
            afterDrawPage();
        }

        if (event != null && event.isRenderShapeData()) {
            renderShapeDataInBackground();
        }
    }

    private void prepareUpdateMode(final RequestFinishEvent event) {
        if (isAnyPopup()) {
            ReaderDeviceManager.resetUpdateMode(surfaceView);
            ReaderDeviceManager.resetUpdateMode(getStatusBar());
            return;
        }

        boolean update = (event != null && event.isApplyGCIntervalUpdate());
        if (update) {
            ReaderDeviceManager.applyWithGCInterval(surfaceView, getReaderDataHolder().getReaderViewInfo().isTextPages());
        } else {
            ReaderDeviceManager.resetUpdateMode(surfaceView);
        }
    }

    private boolean isAnyPopup() {
        if (ShowReaderMenuAction.isReaderMenuShown() || getReaderDataHolder().isAnyActiveDialog()) {
            return true;
        }
        return false;
    }

    private void afterDrawPage() {
        ReaderDeviceManager.cleanUpdateMode(surfaceView);
        updateAllStatusBars();
    }

    private void updateAllStatusBars() {
        updateReadingStatusBar();
        getReaderDataHolder().notifyUpdateSlideshowStatusBar();
    }

    private void holdDisplayUpdate() {
        if (!getStatusBar().isShown()) {
            return;
        }
        ReaderDeviceManager.applyRegalUpdate(this, getStatusBar());
        ReaderDeviceManager.holdDisplay(true);
    }

    @Subscribe
    public void onShapeRendered(final ShapeRenderFinishEvent event) {
        final ReaderNoteDataInfo noteDataInfo = getReaderDataHolder().getNoteManager().getNoteDataInfo();
        if (noteDataInfo == null || !noteDataInfo.isContentRendered()) {
            return;
        }
        if (event.getUniqueId() < getReaderDataHolder().getLastRequestSequence()) {
            return;
        }
        if (event.isUseFullUpdate()) {
            ReaderDeviceManager.applyWithGcUpdate(getSurfaceView());
        }
        beforeDrawPage();
        drawPage(getReaderDataHolder().getReader().getViewportBitmap().getBitmap());
        if (event.isUseFullUpdate()) {
            ReaderDeviceManager.resetUpdateMode(getSurfaceView());
        }
    }

    private boolean verifyReader() {
        return getReaderDataHolder().isDocumentOpened();
    }

    private void beforeDrawPage() {
        holdDisplayUpdate();
        enablePost(true);
    }

    @Subscribe
    public void onSystemUIChanged(final SystemUIChangedEvent event) {
        if (event == null || !getReaderDataHolder().inNoteWritingProvider()) {
            return;
        }
        final List<PageInfo> list = getReaderDataHolder().getVisiblePages();
        if (event.isUiOpen()) {
            FlushNoteAction flushNoteAction = FlushNoteAction.pauseAfterFlush(list);
            flushNoteAction.execute(getReaderDataHolder(), null);
        } else {
            ResumeDrawingAction action = new ResumeDrawingAction(list);
            action.execute(getReaderDataHolder(), null);
        }
        enableShortcut(!event.isUiOpen());
    }

    private void enableShortcut(boolean enable) {
        getReaderDataHolder().getNoteManager().setEnableShortcutDrawing(enable);
        getReaderDataHolder().getNoteManager().setEnableShortcutErasing(enable);
    }

    @Subscribe
    public void onHomeClick(final HomeClickEvent event) {
        if (event == null || !getReaderDataHolder().inNoteWritingProvider()) {
            getReaderDataHolder().getHandlerManager().resetToDefaultProvider();
            saveDocumentOptions();
            return;
        }
        afterPause(null);
    }

    @Subscribe
    public void pinchZoomMenuChanged(final PinchZoomEvent event) {
        if (event == null) {
            return;
        }
        if (event.command == PinchZoomEvent.Command.HIDE) {
            getPinchZoomPopupMenu().hide();
            return;
        }
        if (event.type == PinchZoomEvent.Type.FONT_SIZE) {
            String value = String.format("%d", event.value);
            getPinchZoomPopupMenu().showAndUpdate(PinchZoomingPopupMenu.MessageToShown.FontSize, value);
        } else {
            String value = String.format("%d %%", event.value);
            getPinchZoomPopupMenu().showAndUpdate(PinchZoomingPopupMenu.MessageToShown.ZoomFactor, value);
        }
    }

    private PinchZoomingPopupMenu getPinchZoomPopupMenu() {
        if (pinchZoomingPopupMenu == null) {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            int menuWidth = Math.max(dm.widthPixels, dm.heightPixels) / 4;
            int menuHeight = Math.max(Math.min(dm.widthPixels, dm.heightPixels) / 5, 300);
            pinchZoomingPopupMenu = new PinchZoomingPopupMenu(this,
                    (RelativeLayout)this.findViewById(R.id.main_view),menuWidth,menuHeight);
        }
        return pinchZoomingPopupMenu;
    }

    private void afterPause(final BaseCallback baseCallback) {
        enablePost(true);
        if (!verifyReader()) {
            baseCallback.invoke(baseCallback, null, null);
            return;
        }

        final StopNoteActionChain actionChain = new StopNoteActionChain(false, false, true, false, true, false);
        actionChain.execute(getReaderDataHolder(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                saveDocumentOptions();
                baseCallback.invoke(baseCallback, request, e);
            }
        });
    }

    private void enablePost(boolean enable) {
        EpdController.enablePost(surfaceView, enable ? 1 : 0);
    }

    private void saveDocumentOptions() {
        final SaveDocumentOptionsAction action = new SaveDocumentOptionsAction();
        action.execute(getReaderDataHolder(), null);
    }

    @Subscribe
    public void onShapeDrawing(final ShapeDrawingEvent event) {
        getReaderDataHolder().getNoteManager().ensureContentRendered();
        drawPage(getReaderDataHolder().getReader().getViewportBitmap().getBitmap());
    }

    @Subscribe
    public void onDFBShapeFinished(final ShapeAddedEvent event) {
        final List<PageInfo> list = getReaderDataHolder().getVisiblePages();
        FlushNoteAction flushNoteAction = new FlushNoteAction(list, true, false, false, false);
        flushNoteAction.execute(getReaderDataHolder(), null);
    }

    private void prepareForErasing() {
        if (getReaderDataHolder().getNoteManager().hasShapeStash()) {
            final List<PageInfo> list = getReaderDataHolder().getVisiblePages();
            final FlushNoteAction flushNoteAction = new FlushNoteAction(list, true, true, false, false);
            flushNoteAction.execute(getReaderDataHolder(), null);
            return;
        }
        boolean drawDuringErasing = false;
        if (drawDuringErasing) {
            getReaderDataHolder().getNoteManager().ensureContentRendered();
            drawPage(getReaderDataHolder().getReader().getViewportBitmap().getBitmap());
        }
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
                handleViewActionIntent();
            }
            return true;
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void handleViewActionIntent() {
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        } else {
            openFileFromIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openFileFromIntent();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void openFileFromIntent() {
        Uri uri = getIntent().getData();
        if (uri == null) {
            return;
        }

        final String path = FileUtils.getRealFilePathFromUri(ReaderActivity.this, uri);
        if (isDocumentOpening() || isFileAlreadyOpened(path)) {
            return;
        }

        openFileFromIntentImpl();
    }

    private void openFileFromIntentImpl() {
        final String path = FileUtils.getRealFilePathFromUri(ReaderActivity.this,
                getIntent().getData());

        final OpenDocumentAction action = new OpenDocumentAction(this, path);
        action.execute(getReaderDataHolder(), null);
        releaseStartupWakeLock();
    }

    private boolean isDocumentOpening() {
        return getReaderDataHolder().isDocumentOpening();
    }

    private boolean isFileAlreadyOpened(final String path) {
        if (getReaderDataHolder() == null || StringUtils.isBlank(getReaderDataHolder().getDocumentPath())) {
            return false;
        }
        return getReaderDataHolder().isDocumentOpened() && path.equals(getReaderDataHolder().getDocumentPath());
    }

    private void onSurfaceViewSizeChanged() {
        if (!getReaderDataHolder().isDocumentInitRendered()) {
            return;
        }
        updateNoteHostView();
        new ChangeViewConfigAction().execute(getReaderDataHolder(), null);
    }

    private void updateNoteHostView() {
        getReaderDataHolder().setDisplaySize(surfaceView.getWidth(), surfaceView.getHeight());
        final Rect visibleDrawRect = new Rect();
        surfaceView.getLocalVisibleRect(visibleDrawRect);
        int rotation =  getWindowManager().getDefaultDisplay().getRotation();
        getReaderDataHolder().getNoteManager().updateHostView(this, surfaceView, visibleDrawRect, new Rect(), rotation);
    }

    @Subscribe
    public void onDocumentInitRendered(final DocumentInitRenderedEvent event) {
        initReaderMenu();
        updateNoteHostView();
        getReaderDataHolder().updateRawEventProcessor();
    }

    @Subscribe
    public void onScribbleMenuSizeChanged(final ScribbleMenuChangedEvent event) {
        final Rect rect = new Rect();
        surfaceView.getLocalVisibleRect(rect);
        int bottomOfTopToolBar = event.getBottomOfTopToolBar();
        int topOfBottomToolBar = event.getTopOfBottomToolBar();

        if (bottomOfTopToolBar > 0) {
            rect.top = Math.max(rect.top, bottomOfTopToolBar);
        }
        if (topOfBottomToolBar > 0) {
            rect.bottom = Math.min(rect.bottom, topOfBottomToolBar);
        }

        int rotation =  getWindowManager().getDefaultDisplay().getRotation();
        getReaderDataHolder().getNoteManager().updateHostView(this, surfaceView, rect, event.getExcludeRect(), rotation);
    }

    @Subscribe
    public void onBeforeDocumentOpen(final BeforeDocumentOpenEvent event) {
        enablePost(true);
        clearCanvas(holder);
        resetStatusBar();
        resetMenus();
    }

    @Subscribe
    public void onBeforeDocumentClose(final BeforeDocumentCloseEvent event) {
        enablePost(true);
        resetMenus();
    }

    @Subscribe
    public void onDocumentOpened(final DocumentOpenEvent event) {
        prepareGCUpdateInterval();
        prepareFrontLight();
    }

    private void prepareGCUpdateInterval() {
        int value = DeviceConfig.sharedInstance(this).getGcInterval();
        if (value <= 0) {
            value = DialogScreenRefresh.DEFAULT_INTERVAL_COUNT;
        }
        ReaderDeviceManager.prepareInitialUpdate(LegacySdkDataUtils.getScreenUpdateGCInterval(this, value));
    }

    private void prepareFrontLight() {
        int value = DeviceConfig.sharedInstance(this).getFrontLight();
        if (value <= 0) {
            return;
        }
        FrontLightController.setBrightness(this, value);
    }

    @Subscribe
    public void onChangeOrientation(final ChangeOrientationEvent event) {
        setRequestedOrientation(event.getOrientation());
    }

    @Subscribe
    public void onShortcutErasingStart(final ShortcutErasingStartEvent event) {
        if (!getReaderDataHolder().inNoteWritingProvider()) {
            ShowReaderMenuAction.startNoteDrawing(getReaderDataHolder(), ReaderActivity.this);
        }
    }

    public void onShortcutErasingEvent(final ShortcutErasingEvent event) {

    }

    @Subscribe
    public void onShortcutErasingFinish(final ShortcutErasingFinishEvent event) {
    }

    @Subscribe
    public void onShapeErasing(final ShapeErasingEvent event) {
        if (!event.isFinished()) {
            prepareForErasing();
            return;
        }

        final RemoveShapesByTouchPointListAction action = new RemoveShapesByTouchPointListAction(
                getReaderDataHolder().getVisiblePages(),
                event.getTouchPointList());
        action.execute(getReaderDataHolder(), null);
    }

    @Subscribe
    public void onShortcutDrawingStart(final ShortcutDrawingStartEvent event) {
        getHandlerManager().setEnableTouch(false);
    }

    @Subscribe
    public void onShortcutDrawingFinished(final ShortcutDrawingFinishedEvent event) {
        if (getReaderDataHolder().inNoteWritingProvider()) {
            getHandlerManager().setEnableTouch(true);
            return;
        }
        final List<PageInfo> list = getReaderDataHolder().getVisiblePages();
        FlushNoteAction flushNoteAction = new FlushNoteAction(list, true, true, false, false);
        flushNoteAction.execute(getReaderDataHolder(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                getHandlerManager().setEnableTouch(true);
                ShowReaderMenuAction.startNoteDrawing(getReaderDataHolder(), ReaderActivity.this);
            }
        });
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
        try {
            readerPainter.drawPage(this,
                    canvas,
                    pageBitmap,
                    getReaderDataHolder().getReaderUserDataInfo(),
                    getReaderDataHolder().getReaderViewInfo(),
                    getReaderDataHolder().getSelectionManager(),
                    getReaderDataHolder().getNoteManager());
        } finally {
            holder.unlockCanvasAndPost(canvas);
        }
    }

    private void renderShapeDataInBackground() {
        if (!getReaderDataHolder().supportScalable()) {
            return;
        }

        final ReaderNoteRenderRequest renderRequest = new ReaderNoteRenderRequest(
                getReaderDataHolder().getReader().getDocumentMd5(),
                getReaderDataHolder().getReaderViewInfo().getVisiblePages(),
                getReaderDataHolder().getDisplayRect(),
                false);
        int uniqueId = getReaderDataHolder().getLastRequestSequence();
        getReaderDataHolder().getNoteManager().submitWithUniqueId(this, uniqueId, renderRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null || request.isAbort() ) {
                    return;
                }
                onShapeRendered(ShapeRenderFinishEvent.shapeReadyEventWithUniqueId(renderRequest.getAssociatedUniqueId()));
            }
        });
    }

    public SurfaceHolder getHolder() {
        return holder;
    }

    @Subscribe
    public void onConfirmCloseDialogEvent(final ConfirmCloseDialogEvent event) {
        enableShortcut(!event.isOpen());
    }

    @Subscribe
    public void quitApplication(final QuitEvent event) {
        enablePost(true);
        ShowReaderMenuAction.resetReaderMenu(getReaderDataHolder());
        final CloseActionChain closeAction = new CloseActionChain();
        closeAction.execute(getReaderDataHolder(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                getReaderDataHolder().getEventBus().unregister(this);
                releaseStartupWakeLock();
                finish();
                postFinish();
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

    private void postFinish() {
        boolean exit = DeviceConfig.sharedInstance(this).isExitAfterFinish();
        if (exit) {
            DeviceUtils.exit();
        }
    }

    private boolean hasPopupWindow() {
        return ShowReaderMenuAction.isReaderMenuShown();
    }

    @Subscribe
    private void hideAllPopupMenu(final ClosePopupEvent event) {
        ShowReaderMenuAction.hideReaderMenu();
        ShowTextSelectionMenuAction.hideTextSelectionPopupMenu();
        getReaderDataHolder().closeActiveDialogs();
        getReaderDataHolder().closeNoteMenu();
    }

    private boolean processKeyDown(int keyCode, KeyEvent event) {
        return getHandlerManager().onKeyDown(getReaderDataHolder(), keyCode, event) || super.onKeyDown(keyCode, event);
    }

    private boolean processKeyUp(int keyCode, KeyEvent event) {
        return getHandlerManager().onKeyUp(getReaderDataHolder(), keyCode, event) || super.onKeyUp(keyCode, event);
    }

    private void updateReadingStatusBar() {
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
        int endBatteryPercent = com.onyx.android.sdk.reader.utils.DeviceUtils.getBatteryPecentLevel(getReaderDataHolder().getContext());
        statusBar.updateStatusBar(new ReaderStatusInfo(pageRect, displayRect,
                current, total, endBatteryPercent, title));
    }

    private void resetStatusBar() {
        statusBar.clear();
    }

    private RectF translateDisplayRectToViewportRect(RectF displayRect) {
        RectF rect = new RectF(displayRect);
        rect.intersect(0, 0, getReaderDataHolder().getDisplayWidth(), getReaderDataHolder().getDisplayHeight());
        rect.offset(-displayRect.left, -displayRect.top);
        return rect;
    }

    public void setFullScreen(boolean fullScreen) {
        DeviceUtils.setFullScreenOnResume(this, fullScreen);
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