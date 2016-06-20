package com.onyx.kreader.ui;

import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.*;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.kreader.R;

import com.onyx.kreader.common.*;
import com.onyx.kreader.api.ReaderSelection;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.kreader.host.wrapper.ReaderManager;
import com.onyx.android.sdk.scribble.ShapeViewHelper;
import com.onyx.android.sdk.scribble.data.ShapePage;
import com.onyx.android.sdk.scribble.request.BaseScribbleRequest;
import com.onyx.android.sdk.scribble.request.LoadShapesRequest;
import com.onyx.android.sdk.scribble.request.ShapeDataInfo;
import com.onyx.kreader.ui.actions.*;
import com.onyx.kreader.api.ReaderDocumentOptions;
import com.onyx.kreader.api.ReaderPluginOptions;
import com.onyx.kreader.device.ReaderDeviceManager;
import com.onyx.kreader.host.impl.ReaderDocumentOptionsImpl;
import com.onyx.kreader.host.impl.ReaderPluginOptionsImpl;
import com.onyx.kreader.host.request.PreRenderRequest;
import com.onyx.kreader.host.request.RenderRequest;
import com.onyx.kreader.host.request.SearchRequest;
import com.onyx.kreader.host.request.SelectWordRequest;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.ui.data.PageTurningDetector;
import com.onyx.kreader.ui.data.PageTurningDirection;
import com.onyx.kreader.ui.dialog.PopupSearchMenu;
import com.onyx.kreader.ui.gesture.MyOnGestureListener;
import com.onyx.kreader.ui.gesture.MyScaleGestureListener;
import com.onyx.kreader.ui.handler.HandlerManager;
import com.onyx.kreader.ui.highlight.HighlightCursor;
import com.onyx.kreader.ui.highlight.ReaderSelectionManager;
import com.onyx.kreader.utils.*;

import java.util.List;

/**
 * Created by Joy on 2016/4/14.
 */
public class ReaderActivity extends ActionBarActivity {
    private final static String TAG = ReaderActivity.class.getSimpleName();
    private static final String DOCUMENT_PATH_TAG = "document";

    private String documentPath;
    private Reader reader;
    private ShapeViewHelper shapeViewHelper;

    private SurfaceView surfaceView;
    private SurfaceHolder.Callback surfaceHolderCallback;
    private SurfaceHolder holder;

    private HandlerManager handlerManager;
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleDetector;
    private ReaderViewInfo readerViewInfo;
    private ReaderUserDataInfo readerUserDataInfo;
    private ShapeDataInfo shapeDataInfo;

    private boolean preRender = true;
    private boolean preRenderNext = true;

    private final PixelXorXfermode xorMode = new PixelXorXfermode(Color.WHITE);

    private ReaderSelectionManager selectionManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen(true);
        setContentView(R.layout.activity_reader);
        initActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        ViewTreeObserver observer = surfaceView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                removeGlobalOnLayoutListener(this);
                new ChangeViewConfigAction().execute(ReaderActivity.this);
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
        ShowReaderMenuAction.resetReaderMenu();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return processKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return processKeyUp(keyCode, event);
    }

    public boolean tryHitTest(float x, float y) {
        if (ShowReaderMenuAction.isReaderMenuShown()) {
            ShowReaderMenuAction.hideReaderMenu(this);
            return true;
        }
        return false;
    }

    public int getDisplayWidth() {
        return surfaceView.getWidth();
    }

    public int getDisplayHeight() {
        return surfaceView.getHeight();
    }

    public Rect getDisplayRect() {
        return new Rect(0, 0, getDisplayWidth(), getDisplayHeight());
    }

    public void beforePageChangeByUser() {
    }

    public final Reader getReader() {
        return reader;
    }

    public void nextScreen() {
        preRenderNext = true;
        final NextScreenAction action = new NextScreenAction();
        action.execute(this);
    }

    public void prevScreen() {
        preRenderNext = false;
        final PreviousScreenAction action = new PreviousScreenAction();
        action.execute(this);
    }

    public void preRenderNext() {
        if (!preRender) {
            return;
        }
        final PreRenderRequest request = new PreRenderRequest(preRenderNext);
        getReader().submitRequest(this, request, null);
    }

    public void nextPage() {
        nextScreen();
    }

    public void prevPage() {
        prevScreen();
    }

    public void scaleBegin(ScaleGestureDetector detector) {
        PinchZoomAction.scaleBegin(this, detector);
    }

    public void scaling(ScaleGestureDetector detector) {
        PinchZoomAction.scaling(this, detector);
    }

    public void scaleEnd() {
        PinchZoomAction.scaleEnd(this);
    }

    public void panning(int offsetX, int offsetY) {
        if (!getReaderViewInfo().canPan()) {
            return;
        }
        PanAction.panning(this, offsetX, offsetY);
    }

    public void panFinished(int offsetX, int offsetY) {
        if (!getReaderViewInfo().canPan()) {
            PageTurningDirection direction = PageTurningDetector.detectHorizontalTuring(this, -offsetX);
            if (direction == PageTurningDirection.Left) {
                beforePageChangeByUser();
                prevPage();
            } else if (direction == PageTurningDirection.Right) {
                beforePageChangeByUser();
                nextPage();
            }
            return;
        }

        final PanAction panAction = new PanAction(offsetX, offsetY);
        panAction.execute(this);
    }

    public void highlight(float x1, float y1, float x2, float y2) {

    }

    public void selectWord(float x1, float y1, float x2, float y2, boolean b) {
        PageInfo page = hitTestPage(x1, y1);
        if (page == null) {
            return;
        }
        new SelectWordAction(page.getName(), new PointF(x1, y1), new PointF(x2, y2)).execute(this);
    }

    private PageInfo hitTestPage(float x, float y) {
        if (getReaderViewInfo().getVisiblePages() == null) {
            return null;
        }
        for (PageInfo pageInfo : getReaderViewInfo().getVisiblePages()) {
            if (pageInfo.getDisplayRect().contains(x, y)) {
                return pageInfo;
            }
        }
        return null;
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

    private void initActivity() {
        initToolbar();
        initSurfaceView();
        initHandlerManager();
        initShapeViewDelegate();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_bottom);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.findViewById(R.id.toolbar_backward).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backward();
            }
        });
        toolbar.findViewById(R.id.toolbar_forward).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forward();
            }
        });
        toolbar.findViewById(R.id.toolbar_progress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GotoPageDialogAction().execute(ReaderActivity.this);
            }
        });

        toolbar = (Toolbar)findViewById(R.id.toolbar_top);
        toolbar.findViewById(R.id.toolbar_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowReaderMenuAction.hideReaderMenu(ReaderActivity.this);
                onSearchRequested();
            }
        });
    }

    private void initSurfaceView() {
        surfaceView = (SurfaceView) this.findViewById(R.id.surfaceView);
        surfaceHolderCallback = new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
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
        gestureDetector = new GestureDetector(this, new MyOnGestureListener(this));
        scaleDetector = new ScaleGestureDetector(this, new MyScaleGestureListener(this));
        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                handlerManager.setTouchStartEvent(event);
                scaleDetector.onTouchEvent(event);
                gestureDetector.onTouchEvent(event);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    handlerManager.onActionUp(ReaderActivity.this, event);
                    handlerManager.resetTouchStartPosition();
                }

                handlerManager.onTouchEvent(ReaderActivity.this, event);
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

    private void initHandlerManager() {
        handlerManager = new HandlerManager(this);
        handlerManager.setEnable(false);
    }

    private void initShapeViewDelegate() {
        getShapeViewHelper().setView(surfaceView);
        // when page changed, choose to flush
        //shapeViewHelper.flushPendingShapes();

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
                quitApplication();
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
            new SearchContentAction(getCurrentPageName(), query, forward).execute(this);
        }
    }

    private void openFileFromIntent() {
        Uri uri = getIntent().getData();
        if (uri == null) {
            return;
        }

        final String path = FileUtils.getRealFilePathFromUri(ReaderActivity.this, uri);
        reader = ReaderManager.getReader(path);
        final OpenDocumentAction action = new OpenDocumentAction(path);
        action.execute(this);
    }

    private void gotoPage(int page) {
        final GotoPageAction action = new GotoPageAction(String.valueOf(page));
        action.execute(this);
    }



    public void onDocumentOpened(String path) {
        documentPath = path;
        hideToolbar();
        updateToolbarTitle();
    }

    public void onSearchFinished(SearchRequest request, Throwable e) {
        if (e != null) {
            return;
        }

        PopupSearchMenu.SearchResult result = PopupSearchMenu.SearchResult.EMPTY;
        if (request.getReaderUserDataInfo().hasSearchResults()) {
            result = PopupSearchMenu.SearchResult.SUCCEED;
            onRenderRequestFinished(request, e);
        }
        new ShowSearchMenuAction(request.getSearchOptions(), result).execute(this);
    }

    public void onSelectWordFinished(SelectWordRequest request, Throwable e) {
        if (e != null) {
            return;
        }

        if (!request.getReaderUserDataInfo().hasHighlightResult()) {
            //Toast.makeText(ReaderActivity.this, R.string.emptyselection, Toast.LENGTH_SHORT).show();
            return;
        }

        ReaderSelection selection = request.getReaderUserDataInfo().getHighlightResult();
        Debug.d(TAG, "select word result: " + JSON.toJSONString(selection));
        getSelectionManager().setCurrentSelection(selection);
        getSelectionManager().update(this);
        getSelectionManager().updateDisplayPosition();

        handlerManager.setActiveProvider(HandlerManager.WORD_SELECTION_PROVIDER);
        onRenderRequestFinished(request, e);
    }

    public void backward() {
        final BackwardAction backwardAction = new BackwardAction();
        backwardAction.execute(this);
    }

    public void forward() {
        final ForwardAction forwardAction = new ForwardAction();
        forwardAction.execute(this);
    }

    public void submitRequest(final BaseReaderRequest renderRequest) {
        submitRequest(renderRequest, null);
    }

    public void submitRequest(final BaseReaderRequest renderRequest, final BaseCallback callback) {
        beforeSubmitRequest();
        reader.submitRequest(this, renderRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (callback != null) {
                    callback.done(request, e);
                }
                onRenderRequestFinished(renderRequest, e);
                preRenderNext();
            }
        });
    }

    private void beforeSubmitRequest() {
        resetShapeData();
    }

    private void saveReaderViewInfo(final BaseReaderRequest request) {
        Debug.d(TAG, "saveReaderViewInfo: " + JSON.toJSONString(request.getReaderViewInfo().getFirstVisiblePage()));
        readerViewInfo = request.getReaderViewInfo();
    }

    private void saveReaderUserDataInfo(final BaseReaderRequest request) {
        readerUserDataInfo = request.getReaderUserDataInfo();
    }

    private void saveShapeDataInfo(final BaseScribbleRequest request) {
        shapeDataInfo = request.getShapeDataInfo();
    }

    private boolean hasShapes() {
        if (shapeDataInfo == null) {
            return false;
        }
        return shapeDataInfo.hasShapes();
    }

    public final ReaderViewInfo getReaderViewInfo() {
        return readerViewInfo;
    }

    public final ReaderUserDataInfo getReaderUserDataInfo() {
        return readerUserDataInfo;
    }

    public final ShapeDataInfo getShapeDataInfo() {
        return shapeDataInfo;
    }

    public ReaderSelectionManager getSelectionManager() {
        if (selectionManager == null) {
            selectionManager = new ReaderSelectionManager();
        }
        return selectionManager;
    }

    private void onRenderRequestFinished(final BaseReaderRequest request, Throwable e) {
        Debug.d(TAG, "onRenderRequestFinished: " + request + ", " + e);
        if (e != null || request.isAbort()) {
            return;
        }
        saveReaderViewInfo(request);
        saveReaderUserDataInfo(request);
        updateToolbarProgress();

        //ReaderDeviceManager.applyGCInvalidate(surfaceView);
        drawPage(reader.getViewportBitmap().getBitmap());
        renderShapeDataInBackground();
    }

    public void redrawPage() {
        if (reader != null) {
            submitRequest(new RenderRequest());
        }
    }

    private void drawPage(final Bitmap pageBitmap) {
        Canvas canvas = holder.lockCanvas();
        if (canvas == null) {
            return;
        }
        Paint paint = new Paint();
        drawBackground(canvas, paint);
        drawBitmap(canvas, paint, pageBitmap);
        drawSearchResults(canvas, paint);
        drawHighlightResult(canvas, paint);
        drawShapes(canvas, paint);

        holder.unlockCanvasAndPost(canvas);
    }

    private void drawBackground(Canvas canvas, Paint paint) {
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
    }

    private void drawBitmap(Canvas canvas, Paint paint, Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        canvas.drawBitmap(bitmap, 0, 0, paint);
    }

    private void drawSearchResults(Canvas canvas, Paint paint) {
        drawReaderSelections(canvas, paint, getReaderUserDataInfo().getSearchResults());
    }

    private void drawHighlightResult(Canvas canvas, Paint paint) {
        if (getReaderUserDataInfo().hasHighlightResult()) {
            drawReaderSelection(canvas, paint, getReaderUserDataInfo().getHighlightResult());
            drawSelectionCursor(canvas, paint, xorMode);
        }
    }

    private void drawReaderSelection(Canvas canvas, Paint paint, ReaderSelection selection) {
        PageInfo pageInfo = getReaderViewInfo().getPageInfo(selection.getPagePosition());
        if (pageInfo != null) {
            drawHighlightRectangles(canvas, paint, RectUtils.mergeRectanglesByBaseLine(selection.getRectangles()));
        }
    }

    private void drawReaderSelections(Canvas canvas, Paint paint, List<ReaderSelection> list) {
        if (list == null || list.size() <= 0) {
            return;
        }
        for (ReaderSelection sel : list) {
            drawReaderSelection(canvas, paint, sel);
        }
    }

    private void drawHighlightRectangles(Canvas canvas, Paint paint, List<RectF> rectangles) {
        if (rectangles == null) {
            return;
        }
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setXfermode(xorMode);
        for (int i = 0; i < rectangles.size(); ++i) {
            canvas.drawRect(rectangles.get(i), paint);
        }
    }

    private void drawSelectionCursor(Canvas canvas, Paint paint, PixelXorXfermode xor) {
        getSelectionManager().draw(canvas, paint, xor);
    }

    private void drawShapes(final Canvas canvas, Paint paint) {
        if (!isShapeBitmapReady()) {
            return;
        }
        final Bitmap bitmap = getShapeViewHelper().getShapeBitmap();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
        canvas.drawBitmap(bitmap, 0, 0, paint);
    }

    private ShapeViewHelper getShapeViewHelper() {
        if (shapeViewHelper == null) {
            shapeViewHelper = new ShapeViewHelper();
        }
        return shapeViewHelper;
    }

    private boolean isShapeBitmapReady() {
        // TODO
//        if (!hasShapes()) {
//            return false;
//        }

        final Bitmap bitmap = getShapeViewHelper().getShapeBitmap();
        if (bitmap == null) {
            return false;
        }
        return true;
    }

    private void resetShapeData() {
        getShapeViewHelper().enableBitmap(false);
        shapeDataInfo = null;
    }

    private void renderShapeDataInBackground() {
        if (hasShapes()) {
            return;
        }

        final LoadShapesRequest loadRequest = new LoadShapesRequest(reader.getDocumentMd5(), getReaderViewInfo().getVisiblePages(), getDisplayRect());
        getShapeViewHelper().submit(this, loadRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null || request.isAbort()) {
                    return;
                }
                saveShapeDataInfo(loadRequest);
                getShapeViewHelper().enableBitmap(true);
                drawPage(reader.getViewportBitmap().getBitmap());
            }
        });
    }

    public String getCurrentPageName() {
        return getReaderViewInfo().getFirstVisiblePage().getName();
    }

    public int getCurrentPage() {
        return PagePositionUtils.getPosition(getCurrentPageName());
    }

    public int getPageCount() {
        return reader.getNavigator().getTotalPage();
    }

    public ReaderPluginOptions getPluginOptions() {
        return new ReaderPluginOptionsImpl();
    }

    public ReaderDocumentOptions getDocumentOptions() {
        return new ReaderDocumentOptionsImpl("", "");
    }

    public SurfaceHolder getHolder() {
        return holder;
    }

    public void quitApplication() {

    }

    private void openBuiltInDoc() {
    }

    private boolean hasPopupWindow() {
        return ShowReaderMenuAction.isReaderMenuShown();
    }

    private void hideAllPopupMenu() {
        ShowReaderMenuAction.hideReaderMenu(this);
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
        return handlerManager.onKeyDown(this, keyCode, event) || super.onKeyDown(keyCode, event);
    }

    private boolean processKeyUp(int keyCode, KeyEvent event) {
        return handlerManager.onKeyUp(this, keyCode, event) || super.onKeyUp(keyCode, event);
    }



    public final HandlerManager getHandlerManager() {
        return handlerManager;
    }

    public void showToolbar() {
        findViewById(R.id.toolbar_top).setVisibility(View.VISIBLE);
        findViewById(R.id.toolbar_bottom).setVisibility(View.VISIBLE);
    }

    public void hideToolbar() {
        findViewById(R.id.toolbar_top).setVisibility(View.GONE);
        findViewById(R.id.toolbar_bottom).setVisibility(View.GONE);
    }

    private void updateToolbarTitle() {
        String name = FileUtils.getFileName(documentPath);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_top);
        ((TextView)toolbar.findViewById(R.id.toolbar_title)).setText(name);
    }

    private void updateToolbarProgress() {
        if (readerViewInfo != null && readerViewInfo.getFirstVisiblePage() != null) {
            int pn = Integer.parseInt(readerViewInfo.getFirstVisiblePage().getName());
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_bottom);
            ((TextView) toolbar.findViewById(R.id.toolbar_progress)).setText((pn + 1) + "/" + getPageCount());
        }
    }

    public void setFullScreen(boolean fullScreen) {
        ReaderDeviceManager.setFullScreen(this, fullScreen);
    }

    public boolean hasSelectionWord() {
        return readerUserDataInfo.hasHighlightResult();
    }

    public void highlightAlongTouchMoved(float x, float y, int cursorSelected) {
        ReaderSelection selection = getReaderUserDataInfo().getHighlightResult();
        PageInfo pageInfo = getReaderViewInfo().getPageInfo(selection.getPagePosition());
        if (hitTestPage(x, y) != pageInfo) {
            return;
        }
        if (cursorSelected == HighlightCursor.BEGIN_CURSOR_INDEX) {
            PointF bottomRight = RectUtils.getBottomRight(selection.getRectangles());
            new SelectWordAction(pageInfo.getName(), new PointF(x, y), bottomRight).execute(this);
        } else {
            PointF leftTop = RectUtils.getTopLeft(selection.getRectangles());
            new SelectWordAction(pageInfo.getName(), leftTop, new PointF(x, y)).execute(this);
        }

    }

    public int getCursorSelected(int x, int y) {
        if (getSelectionManager().getHighlightCursor(HighlightCursor.BEGIN_CURSOR_INDEX).hitTest(x, y)) {
            return HighlightCursor.BEGIN_CURSOR_INDEX;
        }
        if (getSelectionManager().getHighlightCursor(HighlightCursor.END_CURSOR_INDEX).hitTest(x, y)) {
            return HighlightCursor.END_CURSOR_INDEX;
        }
        return -1;
    }

    public void quitWordSelection() {
        getHandlerManager().resetToDefaultProvider();
        redrawPage();
    }

    public void showReaderMenu() {
        new ShowReaderMenuAction().execute(this);
    }

    public SurfaceView getSurfaceView() {
        return surfaceView;
    }

    public final PageInfo getFirstPageInfo() {
        return getReaderViewInfo().getFirstVisiblePage();
    }

    public final ShapePage getShapePage() {
        if (shapeDataInfo != null) {
            return shapeDataInfo.getShapePage(getFirstVisiblePageName());
        }
        return null;
    }

    public final String getFirstVisiblePageName() {
        return getReaderViewInfo().getFirstVisiblePage().getName();
    }
}