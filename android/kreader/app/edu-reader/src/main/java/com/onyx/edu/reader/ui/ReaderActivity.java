package com.onyx.edu.reader.ui;

import android.Manifest;
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
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.onyx.android.sdk.api.device.FrontLightController;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.common.request.WakeLockHolder;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.reader.dataprovider.LegacySdkDataUtils;
import com.onyx.android.sdk.reader.utils.TreeObserverUtils;
import com.onyx.android.sdk.ui.data.ReaderStatusInfo;
import com.onyx.android.sdk.ui.view.ReaderStatusBar;
import com.onyx.android.sdk.utils.DeviceUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.utils.ViewDocumentUtils;
import com.onyx.edu.reader.BuildConfig;
import com.onyx.edu.reader.R;
import com.onyx.edu.reader.device.DeviceConfig;
import com.onyx.edu.reader.device.ReaderDeviceManager;
import com.onyx.edu.reader.note.actions.FlushNoteAction;
import com.onyx.edu.reader.note.actions.RemoveShapesByTouchPointListAction;
import com.onyx.edu.reader.note.actions.ResumeDrawingAction;
import com.onyx.edu.reader.note.actions.StopNoteActionChain;
import com.onyx.edu.reader.note.data.ReaderNoteDataInfo;
import com.onyx.edu.reader.note.request.ReaderNoteRenderRequest;
import com.onyx.edu.reader.ui.actions.BackwardAction;
import com.onyx.edu.reader.ui.actions.ChangeViewConfigAction;
import com.onyx.edu.reader.ui.actions.CloseActionChain;
import com.onyx.edu.reader.ui.actions.ForwardAction;
import com.onyx.edu.reader.ui.actions.OpenDocumentAction;
import com.onyx.edu.reader.ui.actions.SaveDocumentOptionsAction;
import com.onyx.edu.reader.ui.actions.ShowQuickPreviewAction;
import com.onyx.edu.reader.ui.actions.ShowReaderMenuAction;
import com.onyx.edu.reader.ui.actions.ShowTextSelectionMenuAction;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;
import com.onyx.edu.reader.ui.data.SingletonSharedPreference;
import com.onyx.edu.reader.ui.dialog.DialogScreenRefresh;
import com.onyx.edu.reader.ui.events.BeforeDocumentCloseEvent;
import com.onyx.edu.reader.ui.events.BeforeDocumentOpenEvent;
import com.onyx.edu.reader.ui.events.ChangeEpdUpdateModeEvent;
import com.onyx.edu.reader.ui.events.ChangeOrientationEvent;
import com.onyx.edu.reader.ui.events.ClosePopupEvent;
import com.onyx.edu.reader.ui.events.ConfirmCloseDialogEvent;
import com.onyx.edu.reader.ui.events.DocumentInitRenderedEvent;
import com.onyx.edu.reader.ui.events.DocumentOpenEvent;
import com.onyx.edu.reader.ui.events.ForceCloseEvent;
import com.onyx.edu.reader.ui.events.LayoutChangeEvent;
import com.onyx.edu.reader.ui.events.MoveTaskToBackEvent;
import com.onyx.edu.reader.ui.events.OpenDocumentFailedEvent;
import com.onyx.edu.reader.ui.events.PinchZoomEvent;
import com.onyx.edu.reader.ui.events.QuitEvent;
import com.onyx.edu.reader.ui.events.RequestFinishEvent;
import com.onyx.edu.reader.ui.events.ResetEpdUpdateModeEvent;
import com.onyx.edu.reader.ui.events.ResizeReaderWindowEvent;
import com.onyx.edu.reader.ui.events.ScribbleMenuChangedEvent;
import com.onyx.edu.reader.ui.events.ShapeAddedEvent;
import com.onyx.edu.reader.ui.events.ShapeDrawingEvent;
import com.onyx.edu.reader.ui.events.ShapeErasingEvent;
import com.onyx.edu.reader.ui.events.ShapeRenderFinishEvent;
import com.onyx.edu.reader.ui.events.ShortcutDrawingFinishedEvent;
import com.onyx.edu.reader.ui.events.ShortcutDrawingStartEvent;
import com.onyx.edu.reader.ui.events.ShortcutErasingEvent;
import com.onyx.edu.reader.ui.events.ShortcutErasingFinishEvent;
import com.onyx.edu.reader.ui.events.ShortcutErasingStartEvent;
import com.onyx.edu.reader.ui.events.ShowReaderSettingsEvent;
import com.onyx.edu.reader.ui.events.DocumentActivatedEvent;
import com.onyx.edu.reader.ui.events.SlideshowStartEvent;
import com.onyx.edu.reader.ui.events.SystemUIChangedEvent;
import com.onyx.edu.reader.ui.events.UpdateScribbleMenuEvent;
import com.onyx.edu.reader.ui.events.UpdateTabWidgetVisibilityEvent;
import com.onyx.edu.reader.ui.gesture.MyOnGestureListener;
import com.onyx.edu.reader.ui.gesture.MyScaleGestureListener;
import com.onyx.edu.reader.ui.handler.BaseHandler;
import com.onyx.edu.reader.ui.handler.HandlerManager;
import com.onyx.edu.reader.ui.handler.SlideshowHandler;
import com.onyx.edu.reader.ui.receiver.NetworkConnectChangedReceiver;
import com.onyx.edu.reader.ui.settings.MainSettingsActivity;
import com.onyx.edu.reader.ui.view.PinchZoomingPopupMenu;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joy on 2016/4/14.
 */
public class ReaderActivity extends OnyxBaseActivity {
    private static final String DOCUMENT_PATH_TAG = "document";

    private WakeLockHolder startupWakeLock = new WakeLockHolder();
    private SurfaceView surfaceView;
    private RelativeLayout mainView;
    private RelativeLayout extraView;
    private ImageView buttonShowTabWidget;
    private SurfaceHolder.Callback surfaceHolderCallback;
    private SurfaceHolder holder;
    private ReaderStatusBar statusBar;

    private ReaderDataHolder dataHolder;
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleDetector;
    private NetworkConnectChangedReceiver networkConnectChangedReceiver;
    private final ReaderPainter readerPainter = new ReaderPainter();

    private PinchZoomingPopupMenu pinchZoomingPopupMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        acquireStartupWakeLock();
        setContentView(R.layout.activity_reader);
        initWindow();
        initComponents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        afterResume();
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
    protected void onPause() {
        if (DeviceUtils.isDeviceInteractive(this)) {
            onDocumentDeactivated();
        }
        getReaderDataHolder().onActivityPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        cleanupReceiver();
        ReaderActivity.super.onDestroy();
        if (getReaderDataHolder().isDocumentOpened()) {
            forceCloseApplication(null);
        }
        releaseStartupWakeLock();
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

    @Override
    public void onBackPressed() {
        ReaderTabHostBroadcastReceiver.sendTabBackPressedIntent(this);
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

    private void initWindow() {
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.height = getIntent().getIntExtra(ReaderBroadcastReceiver.TAG_WINDOW_HEIGHT, WindowManager.LayoutParams.MATCH_PARENT);
        layoutParams.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        getWindow().setAttributes(layoutParams);

        Debug.d(getClass(), "target window height:" + layoutParams.height);
    }

    private void initComponents() {
        initTabButtons();
        initStatusBar();
        initReaderDataHolder();
        initSurfaceView();
        initReceiver();
    }

    private void initTabButtons() {
        buttonShowTabWidget = (ImageView) findViewById(R.id.button_show_tab_widget);
        buttonShowTabWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonShowTabWidget.setVisibility(View.GONE);
                ReaderTabHostBroadcastReceiver.sendShowTabWidgetEvent(ReaderActivity.this);
            }
        });
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

    private void cleanupReceiver() {
        if (networkConnectChangedReceiver != null) {
            unregisterReceiver(networkConnectChangedReceiver);
            networkConnectChangedReceiver = null;
        }
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
        extraView = (RelativeLayout) findViewById(R.id.extra_view);
        surfaceView = (SurfaceView) this.findViewById(R.id.surfaceView);
        surfaceHolderCallback = new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Debug.d(getClass(), "surfaceCreated");
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Debug.d(getClass(), "surfaceChanged: " + format + ", " + width + ", " + height);
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
                Debug.d(getClass(), "surfaceDestroyed");
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
                if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    getHandlerManager().onActionCancel(getReaderDataHolder(), event);
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
        updateNoteState();
        getReaderDataHolder().onActivityResume();
    }

    private void enablePenShortcut() {
        getReaderDataHolder().enablePenShortcut();
    }

    private void disablePenShortcut() {
        getReaderDataHolder().disablePenShortcut();
    }

    private void stopRawEventProcessor() {
        getReaderDataHolder().stopRawEventProcessor();
    }

    private void updateNoteState() {
        if (getReaderDataHolder().inNoteWritingProvider()) {
            return;
        }
        updateNoteHostView();
        getReaderDataHolder().updateRawEventProcessor();
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
        ReaderDeviceManager.disableRegal();
    }

    @Subscribe
    public void onResetEpdUpdateMode(final ResetEpdUpdateModeEvent event) {
        ReaderDeviceManager.disableRegal();
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
            ReaderDeviceManager.disableRegal();
            return;
        }

        boolean update = (event != null && event.isApplyGCIntervalUpdate());
        if (update) {
            ReaderDeviceManager.applyWithGCInterval(surfaceView, getReaderDataHolder().getReaderViewInfo().isTextPages());
        } else {
            ReaderDeviceManager.disableRegal();
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
            ReaderDeviceManager.disableRegal();
        }
    }

    private boolean verifyReader() {
        return getReaderDataHolder().isDocumentOpened();
    }

    private void beforeDrawPage() {
        ReaderDeviceManager.holdDisplayUpdate(this, getStatusBar());
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

    @Subscribe
    public void onDocumentActivated(final DocumentActivatedEvent event) {
        if (getReaderDataHolder() == null) {
            return;
        }
        if (!getReaderDataHolder().isDocumentOpened() ||
                getReaderDataHolder().getDocumentPath().contains(event.getActiveDocPath())) {
            return;
        }
    }

    @Subscribe
    public void onUpdateTabWidgetVisibility(final UpdateTabWidgetVisibilityEvent event) {
        Debug.d(getClass(), "onUpdateTabWidgetVisibility: " + event.visible);
        if (!event.visible) {
            buttonShowTabWidget.setVisibility(View.VISIBLE);
        } else {
            buttonShowTabWidget.setVisibility(View.GONE);
        }
        getReaderDataHolder().getEventBus().post(new UpdateScribbleMenuEvent());
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

    private void onDocumentDeactivated() {
        enablePost(true);
        if (!verifyReader()) {
            return;
        }

        disablePenShortcut();
        stopRawEventProcessor();
        getReaderDataHolder().getHandlerManager().resetToDefaultProvider();
        if (!getReaderDataHolder().isDocumentOpened()) {
            return;
        }
        if (!getReaderDataHolder().inNoteWritingProvider()) {
            saveDocumentOptions();
        } else {
            stopNoteWriting(new BaseCallback() {
                @Override
                public void done(BaseRequest request, Throwable e) {
                    saveDocumentOptions();
                }
            });
        }
    }

    private void enablePost(boolean enable) {
        EpdController.enablePost(surfaceView, enable ? 1 : 0);
    }

    private void saveDocumentOptions() {
        final SaveDocumentOptionsAction action = new SaveDocumentOptionsAction();
        action.execute(getReaderDataHolder(), null);
    }

    private void stopNoteWriting(BaseCallback callback) {
        final StopNoteActionChain actionChain = new StopNoteActionChain(false, false, true, false, true, false);
        actionChain.execute(getReaderDataHolder(), callback);
    }

    @Subscribe
    public void onShapeDrawing(final ShapeDrawingEvent event) {
        getReaderDataHolder().getNoteManager().ensureContentRendered();
        drawPage(getReaderDataHolder().getReader().getViewportBitmap().getBitmap());
    }

    @Subscribe
    public void onDFBShapeFinished(final ShapeAddedEvent event) {
        flushReaderNote(true, false, false, false, null);
    }

    private void prepareForErasing() {
        if (getReaderDataHolder().getNoteManager().hasShapeStash()) {
            flushReaderNote(true, true, false, false, null);
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
        startupWakeLock.acquireWakeLock(this, ReaderActivity.class.getSimpleName());
    }

    private void releaseStartupWakeLock() {
        startupWakeLock.releaseWakeLock();
    }

    private boolean handleActivityIntent() {
        try {
            String action = getIntent().getAction();
            if (action.equals(Intent.ACTION_VIEW)) {
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

        if (getReaderDataHolder().inNoteWritingProvider()) {
            flushReaderNote(true, true, true, false, new BaseCallback() {
                @Override
                public void done(BaseRequest request, Throwable e) {
                    changeViewConfig();
                }
            });
        }else {
            changeViewConfig();
        }
    }

    private void flushReaderNote(boolean renderShapes, boolean transferBitmap, boolean saveToDatabase, boolean show, final BaseCallback callback) {
        final List<PageInfo> list = getReaderDataHolder().getVisiblePages();
        FlushNoteAction flushNoteAction = new FlushNoteAction(list, renderShapes, transferBitmap, saveToDatabase, show);
        flushNoteAction.execute(getReaderDataHolder(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                BaseCallback.invoke(callback, request, e);
            }
        });
    }

    private void changeViewConfig() {
        new ChangeViewConfigAction().execute(getReaderDataHolder(), null);
    }

    private void updateNoteHostView() {
        getReaderDataHolder().setDisplaySize(surfaceView.getWidth(), surfaceView.getHeight());
        final Rect visibleDrawRect = new Rect();
        surfaceView.getLocalVisibleRect(visibleDrawRect);
        int rotation =  getWindowManager().getDefaultDisplay().getRotation();
        getReaderDataHolder().getNoteManager().updateHostView(this, surfaceView, visibleDrawRect, new ArrayList<RectF>(), rotation);
    }

    @Subscribe
    public void onDocumentInitRendered(final DocumentInitRenderedEvent event) {
        initReaderMenu();
        updateNoteHostView();
        getReaderDataHolder().updateRawEventProcessor();

        postDocumentInitRendered();
    }

    private void postDocumentInitRendered() {
        if (getIntent().getBooleanExtra(ViewDocumentUtils.TAG_AUTO_SLIDE_SHOW_MODE, false)) {
            int maxPageCount = getIntent().getIntExtra(ViewDocumentUtils.TAG_SLIDE_SHOW_MAX_PAGE_COUNT, 2000);
            int interval = getIntent().getIntExtra(ViewDocumentUtils.TAG_SLIDE_SHOW_INTERVAL_IN_SECONDS, 5);
            BaseHandler.HandlerInitialState state = SlideshowHandler.createInitialState(mainView, maxPageCount, interval);
            getHandlerManager().setActiveProvider(HandlerManager.SLIDESHOW_PROVIDER, state);
        }

        Debug.setDebug(getIntent().getBooleanExtra(ReaderBroadcastReceiver.TAG_ENABLE_DEBUG, Debug.getDebug()));

        boolean tabWidgetVisible = getIntent().getBooleanExtra(ReaderBroadcastReceiver.TAG_TAB_WIDGET_VISIBLE,
                true);
        Debug.d(getClass(), "postDocumentInitRendered: tab widget visible -> " + tabWidgetVisible);
        if (!tabWidgetVisible) {
            buttonShowTabWidget.setVisibility(View.VISIBLE);
        }
        releaseStartupWakeLock();
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
        getReaderDataHolder().getNoteManager().updateHostView(this, surfaceView, rect, getExcludeRect(event.getExcludeRect()), rotation);
    }

    private List<RectF> getExcludeRect(final RectF scribbleMenuExcludeRect) {
        List<RectF> excludeRect = new ArrayList<>();
        excludeRect.add(scribbleMenuExcludeRect);
        if (buttonShowTabWidget.isShown()) {
            RectF r = new RectF(buttonShowTabWidget.getLeft(), buttonShowTabWidget.getTop(), buttonShowTabWidget.getRight(), buttonShowTabWidget.getBottom());
            excludeRect.add(r);
        }
        return excludeRect;
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

    @Subscribe
    public void onResizeReaderWindow(final ResizeReaderWindowEvent event) {
        Debug.d(getClass(), "onResizeReaderWindow: " + event.width + ", " + event.height);
        getWindow().setLayout(event.width, event.height);
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
        ReaderTabHostBroadcastReceiver.sendChangeOrientationIntent(this, event.getOrientation());
    }

    @Subscribe
    public void onShortcutErasingStart(final ShortcutErasingStartEvent event) {
        if (!getReaderDataHolder().inNoteWritingProvider()) {
            ShowReaderMenuAction.startNoteDrawing(getReaderDataHolder(), ReaderActivity.this, true);
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
        flushReaderNote(true, true, false, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                getHandlerManager().setEnableTouch(true);
                ShowReaderMenuAction.startNoteDrawing(getReaderDataHolder(), ReaderActivity.this, true);
            }
        });
    }

    @Subscribe
    public void onEnterSlideShow(final SlideshowStartEvent event) {
        ShowReaderMenuAction.enterSlideshow(getReaderDataHolder(), ReaderActivity.this);
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
    public void quitApplication(final QuitEvent event) {
        onBackPressed();
    }

    @Subscribe
    public void onConfirmCloseDialogEvent(final ConfirmCloseDialogEvent event) {
        enableShortcut(!event.isOpen());
    }

    @Subscribe
    public void onOpenDocumentFailed(final OpenDocumentFailedEvent event) {
        enablePost(true);
        ShowReaderMenuAction.resetReaderMenu(getReaderDataHolder());
        getReaderDataHolder().getEventBus().unregister(this);
        releaseStartupWakeLock();
        ReaderTabHostBroadcastReceiver.sendOpenDocumentFailedEvent(this, getReaderDataHolder().getDocumentPath());

        finish();
        postFinish();
    }

    @Subscribe
    public void forceCloseApplication(final ForceCloseEvent event) {
        enablePost(true);
        ShowReaderMenuAction.resetReaderMenu(getReaderDataHolder());
        if (event != null && event.byUser) {
            onBackPressed();
        }

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

    @Subscribe
    public void onMoveTaskToBackRequest(final MoveTaskToBackEvent event) {
        moveTaskToBack(true);
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
        DeviceUtils.exit();
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
        int endBatteryPercent = DeviceUtils.getBatteryPecentLevel(getReaderDataHolder().getContext());
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
        if (fullScreen) {
            ReaderTabHostBroadcastReceiver.sendEnterFullScreenIntent(this);
        } else {
            ReaderTabHostBroadcastReceiver.sendQuitFullScreenIntent(this);
        }
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

    public RelativeLayout getExtraView() {
        return extraView;
    }
}