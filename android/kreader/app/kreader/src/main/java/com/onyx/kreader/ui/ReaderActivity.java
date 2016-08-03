package com.onyx.kreader.ui;

import android.app.SearchManager;
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
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.request.navigation.PageListRenderRequest;
import com.onyx.android.sdk.ui.data.ReaderStatusInfo;
import com.onyx.android.sdk.ui.view.ReaderStatusBar;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.R;
import com.onyx.kreader.common.Debug;
import com.onyx.kreader.dataprovider.compatability.LegacySdkDataUtils;
import com.onyx.kreader.device.ReaderDeviceManager;
import com.onyx.kreader.host.wrapper.ReaderManager;
import com.onyx.kreader.ui.actions.BackwardAction;
import com.onyx.kreader.ui.actions.ChangeViewConfigAction;
import com.onyx.kreader.ui.actions.ForwardAction;
import com.onyx.kreader.ui.actions.GotoPageAction;
import com.onyx.kreader.ui.actions.OpenDocumentAction;
import com.onyx.kreader.ui.actions.SearchContentAction;
import com.onyx.kreader.ui.actions.ShowQuickPreviewAction;
import com.onyx.kreader.ui.actions.ShowReaderMenuAction;
import com.onyx.kreader.ui.actions.ShowSearchMenuAction;
import com.onyx.kreader.ui.actions.ShowTextSelectionMenuAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.dialog.DialogScreenRefresh;
import com.onyx.kreader.ui.events.ChangeOrientationEvent;
import com.onyx.kreader.ui.events.DocumentOpenEvent;
import com.onyx.kreader.ui.events.RequestFinishEvent;
import com.onyx.kreader.ui.events.QuitEvent;
import com.onyx.kreader.ui.gesture.MyOnGestureListener;
import com.onyx.kreader.ui.gesture.MyScaleGestureListener;
import com.onyx.kreader.ui.handler.HandlerManager;
import com.onyx.kreader.utils.TreeObserverUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Joy on 2016/4/14.
 */
public class ReaderActivity extends ActionBarActivity {
    private final static String TAG = ReaderActivity.class.getSimpleName();
    private static final String DOCUMENT_PATH_TAG = "document";

    private String documentPath;

    private SurfaceView surfaceView;
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
        setFullScreen(true);
        setContentView(R.layout.activity_reader);
        initComponents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getReaderDataHolder().redrawPage();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        ViewTreeObserver observer = surfaceView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                removeGlobalOnLayoutListener(this);
                getReaderDataHolder().setDisplayHeight(surfaceView.getHeight());
                getReaderDataHolder().setDisplayWidth(surfaceView.getWidth());
                new ChangeViewConfigAction().execute(getReaderDataHolder());
            }
        });

        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void startActivity(Intent intent) {
        // check if search intent
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            intent.putExtra(DOCUMENT_PATH_TAG, documentPath);
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
        resetMenus();
        super.onDestroy();
    }

    private void resetMenus() {
        ShowReaderMenuAction.hideReaderMenu();
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
        initShapeViewDelegate();
    }

    private void initStatusBar() {
        statusBar = (ReaderStatusBar)findViewById(R.id.status_bar);
        statusBar.setCallback(new ReaderStatusBar.Callback() {
            @Override
            public void onGotoPage() {
                new ShowQuickPreviewAction().execute(readerDataHolder);
            }
        });
    }

    private void initSurfaceView() {
        surfaceView = (SurfaceView) this.findViewById(R.id.surfaceView);
        surfaceHolderCallback = new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                getReaderDataHolder().setDisplayHeight(surfaceView.getHeight());
                getReaderDataHolder().setDisplayWidth(surfaceView.getWidth());
                clearCanvas(holder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                clearCanvas(holder);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                holder.removeCallback(surfaceHolderCallback);
                Log.i(TAG, "surface destroyed");
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

    private void initShapeViewDelegate() {
//        getNoteViewHelper().setView(this, surfaceView, null);
        // when page changed, choose to flush
        //noteViewHelper.flushPendingShapes();
    }

    @Subscribe
    public void onRequestFinished(final RequestFinishEvent event) {
        ReaderDeviceManager.applyWithGCInterval(surfaceView);
        drawPage(getReaderDataHolder().getReader().getViewportBitmap().getBitmap());
        updateStatusBar();
        renderShapeDataInBackground();
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

    private boolean handleActivityIntent() {
        try {
            String action = getIntent().getAction();
            if (StringUtils.isNullOrEmpty(action)) {
                openBuiltInDoc();
            } else if (action.equals(Intent.ACTION_MAIN)) {
                quitApplication(null);
            } else if (action.equals(Intent.ACTION_VIEW)) {
                openFileFromIntent();
            } else if (action.equals(Intent.ACTION_SEARCH)) {
                searchContent(getIntent(), true);
            }
            return true;
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void searchContent(Intent intent, boolean forward) {
        final String query = intent.getStringExtra(SearchManager.QUERY);
        if (StringUtils.isNotBlank(query)) {
            new SearchContentAction(getReaderDataHolder().getCurrentPageName(), query, forward).execute(getReaderDataHolder());
        }
    }

    private void openFileFromIntent() {
        Uri uri = getIntent().getData();
        if (uri == null) {
            return;
        }

        final String path = FileUtils.getRealFilePathFromUri(ReaderActivity.this, uri);
        getReaderDataHolder().setReader(ReaderManager.getReader(path));
        final OpenDocumentAction action = new OpenDocumentAction(path);
        action.execute(getReaderDataHolder());
    }

    private void gotoPage(int page) {
        final GotoPageAction action = new GotoPageAction(String.valueOf(page));
        action.execute(getReaderDataHolder());
    }

    @Subscribe
    public void onDocumentOpened(final DocumentOpenEvent event) {
        documentPath = event.getPath();
        ReaderDeviceManager.prepareInitialUpdate(LegacySdkDataUtils.getScreenUpdateGCInterval(this,
                DialogScreenRefresh.DEFAULT_INTERVAL_COUNT));
    }

    @Subscribe
    public void onChangeOrientation(final ChangeOrientationEvent event) {
        setRequestedOrientation(event.getOrientation());
    }

    public void backward() {
        final BackwardAction backwardAction = new BackwardAction();
        backwardAction.execute(getReaderDataHolder());
    }

    public void forward() {
        final ForwardAction forwardAction = new ForwardAction();
        forwardAction.execute(getReaderDataHolder());
    }

    private void drawPage(final Bitmap pageBitmap) {
        // lock dirty region instead of whole surface view, which will cause strange duplicated GC update issue
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
                getReaderDataHolder().getNoteViewHelper(),
                getReaderDataHolder().getShapeDataInfo());
        holder.unlockCanvasAndPost(canvas);
    }

    private boolean isShapeBitmapReady() {
        // TODO
//        if (!hasShapes()) {
//            return false;
//        }

        final Bitmap bitmap = getReaderDataHolder().getNoteViewHelper().getViewBitmap();
        if (bitmap == null) {
            return false;
        }
        return true;
    }

    private void renderShapeDataInBackground() {
        if (true || getReaderDataHolder().hasShapes()) {
            return;
        }
        final PageListRenderRequest loadRequest = new PageListRenderRequest(
                getReaderDataHolder().getReader().getDocumentMd5(),
                getReaderDataHolder().getReaderViewInfo().getVisiblePages(),
                getReaderDataHolder().getDisplayRect());
        getReaderDataHolder().getNoteViewHelper().submit(this, loadRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null || request.isAbort()) {
                    return;
                }
                getReaderDataHolder().saveShapeDataInfo(loadRequest);
                onRequestFinished(null);
            }
        });
    }

    public SurfaceHolder getHolder() {
        return holder;
    }

    @Subscribe
    public void quitApplication(final QuitEvent event) {
        finish();
    }

    private void openBuiltInDoc() {
    }

    private boolean hasPopupWindow() {
        return ShowReaderMenuAction.isReaderMenuShown();
    }

    private void hideAllPopupMenu() {
        ShowReaderMenuAction.hideReaderMenu();
    }

    protected boolean askForClose() {
        return false;
    }

    private boolean processKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!hasPopupWindow()){
                if (askForClose()) {
                    return true;
                }
            } else {
                hideAllPopupMenu();
                return true;
            }
        }
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
        Debug.d("pageRect: " + JSON.toJSON(pageRect));
        Debug.d("displayRect: " + JSON.toJSON(displayRect));
        int current = getReaderDataHolder().getCurrentPage() + 1;
        int total = getReaderDataHolder().getPageCount();
        String title = getReaderDataHolder().getBookName();
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
        ReaderDeviceManager.setFullScreen(this, fullScreen);
    }

    public SurfaceView getSurfaceView() {
        return surfaceView;
    }
}