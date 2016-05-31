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
import android.widget.LinearLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.onyx.kreader.R;
import com.onyx.kreader.common.BaseRequest;
import com.onyx.kreader.api.ReaderSelection;
import com.onyx.kreader.common.Debug;
import com.onyx.kreader.common.ReaderViewInfo;
import com.onyx.kreader.dataprovider.DataProvider;
import com.onyx.kreader.host.math.PageInfo;
import com.onyx.kreader.host.wrapper.ReaderManager;
import com.onyx.kreader.ui.actions.*;
import com.onyx.kreader.api.ReaderDocumentOptions;
import com.onyx.kreader.api.ReaderPluginOptions;
import com.onyx.kreader.common.BaseCallback;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.device.ReaderDeviceManager;
import com.onyx.kreader.host.impl.ReaderDocumentOptionsImpl;
import com.onyx.kreader.host.impl.ReaderPluginOptionsImpl;
import com.onyx.kreader.host.navigation.NavigationArgs;
import com.onyx.kreader.host.options.ReaderConstants;
import com.onyx.kreader.host.request.*;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.ui.gesture.MyOnGestureListener;
import com.onyx.kreader.ui.gesture.MyScaleGestureListener;
import com.onyx.kreader.ui.handler.HandlerManager;
import com.onyx.kreader.ui.menu.ReaderMenu;
import com.onyx.kreader.ui.menu.ReaderMenuItem;
import com.onyx.kreader.ui.menu.ReaderSideMenu;
import com.onyx.kreader.ui.menu.ReaderSideMenuItem;
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
    private DataProvider dataProvider;

    private SurfaceView surfaceView;
    private SurfaceHolder.Callback surfaceHolderCallback;
    private SurfaceHolder holder;

    private ReaderMenu readerMenu;

    private HandlerManager handlerManager;
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleDetector;
    private ReaderViewInfo readerViewInfo;

    private boolean preRender = true;
    private boolean preRenderNext = true;

    private final PixelXorXfermode xorMode = new PixelXorXfermode(Color.WHITE);

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
        if (isReaderMenuShown()) {
            hideReaderMenu();
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

    public void beforePageChangeByUser() {
    }

    public final Reader getReader() {
        return reader;
    }

    public final DataProvider getDataProvider() {
        return dataProvider;
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
        reader.submitRequest(this, request, null);
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
            return;
        }

        final PanAction panAction = new PanAction(offsetX, offsetY);
        panAction.execute(this);
    }

    public void highlight(float x1, float y1, float x2, float y2) {

    }

    public void selectWord(float x1, float y1, float x2, float y2, boolean b) {

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
        initReaderMenu();
        initHandlerManager();
        initDataProvider();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_bottom);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
                hideReaderMenu();
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

    private void initDataProvider() {
        dataProvider = new DataProvider();
    }

    private void initReaderMenu() {
        LinearLayout layout = (LinearLayout)findViewById(R.id.left_drawer);
        createReaderSideMenu(layout);
    }

    private void createReaderSideMenu(LinearLayout drawerLayout) {
        readerMenu = new ReaderSideMenu(this, drawerLayout);
        readerMenu.setReaderMenuCallback(new ReaderMenu.ReaderMenuCallback() {
            @Override
            public void onMenuItemClicked(ReaderMenuItem menuItem) {
                Log.d(TAG, "onMenuItemClicked: " + menuItem.getURI().getRawPath());
                switch (menuItem.getURI().getRawPath()) {
                    case "/Rotation/Rotation0":
                        rotateScreen(0);
                        break;
                    case "/Rotation/Rotation90":
                        rotateScreen(90);
                        break;
                    case "/Rotation/Rotation180":
                        rotateScreen(180);
                        break;
                    case "/Rotation/Rotation270":
                        rotateScreen(270);
                        break;
                    case "/Zoom/ZoomIn":
                        scaleUp();
                        break;
                    case "/Zoom/ZoomOut":
                        scaleDown();
                        break;
                    case "/Zoom/ToPage":
                        scaleToPage();
                        break;
                    case "/Zoom/ToWidth":
                        scaleToWidth();
                        break;
                    case "/Zoom/ByRect":
                        scaleByRect();
                        break;
                    case "/Zoom/Crop":
                        scaleByAutoCrop();
                        break;
                    case "/Navigation/ArticleMode":
                        switchNavigationToArticleMode();
                        break;
                    case "/Navigation/ComicMode":
                        switchNavigationToComicMode();
                        break;
                    case "/Navigation/Reset":
                        resetNavigationMode();
                        break;
                    case "/Navigation/MoreSetting":
                        break;
                    case "/Spacing/DecreaseSpacing":
                        break;
                    case "/Spacing/EnlargeSpacing":
                        break;
                    case "/Spacing/NormalSpacing":
                        break;
                    case "/Spacing/SmallSpacing":
                        break;
                    case "/Spacing/LargeSpacing":
                        break;
                    case "/Spacing/Indent":
                        break;
                    case "/Font/DecreaseSpacing":
                        break;
                    case "/Font/IncreaseSpacing":
                        break;
                    case "/Font/Gamma":
                        adjustContrast();
                        break;
                    case "/Font/Embolden":
                        adjustEmbolden();
                        break;
                    case "/Font/FontReflow":
                        imageReflow();
                        break;
                    case "/Font/TOC":
                        break;
                    case "/Font/Bookmark":
                        break;
                    case "/Font/Note":
                        break;
                    case "/Font/Scribble":
                        break;
                    case "/Font/Export":
                        break;
                    case "/Exit":
                        onBackPressed();
                        break;
                }
            }
        });
        List<ReaderSideMenuItem> items = createReaderSideMenuItems();
        readerMenu.fillItems(items);
    }

    private void rotateScreen(int rotationOperation) {
        final ChangeOrientationAction action = new ChangeOrientationAction(rotationOperation);
        action.execute(this);
    }

    private List<ReaderSideMenuItem> createReaderSideMenuItems() {
        JSONObject json = JSON.parseObject(RawResourceUtil.contentOfRawResource(this, R.raw.reader_menu));
        JSONArray array = json.getJSONArray("menu_list");
        return ReaderSideMenuItem.createFromJSON(this, array);
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
            new SearchContentAction(query, forward).execute(this);
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

    private void scaleUp() {
        final ChangeScaleWithDeltaAction action = new ChangeScaleWithDeltaAction(0.1f);
        action.execute(this);
    }

    private void scaleDown() {
        final ChangeScaleWithDeltaAction action = new ChangeScaleWithDeltaAction(-0.1f);
        action.execute(this);
    }

    private void scaleByValue(float scale) {
        final ScaleRequest request = new ScaleRequest(getCurrentPageName(), scale, getDisplayWidth() / 2, getDisplayHeight() / 2);
        submitRenderRequest(request);
    }

    private void scaleToPage() {
        final ScaleToPageRequest request = new ScaleToPageRequest(getCurrentPageName());
        submitRenderRequest(request);
    }

    private void scaleToWidth() {
        final ScaleToWidthRequest request = new ScaleToWidthRequest(getCurrentPageName());
        submitRenderRequest(request);
    }

    private void scaleByRect() {
        final SelectionScaleAction action = new SelectionScaleAction();
        action.execute(this);
    }

    private void scaleByAutoCrop() {
        final PageCropAction action = new PageCropAction(getCurrentPageName());
        action.execute(this);
    }

    private void switchNavigationToArticleMode() {
        NavigationArgs args = new NavigationArgs();
        RectF limit = new RectF(0, 0, 0, 0);
        args.columnsLeftToRight(NavigationArgs.Type.ALL, 2, 2, limit);
        switchPageNavigationMode(args);
    }

    private void switchNavigationToComicMode() {
        NavigationArgs args = new NavigationArgs();
        RectF limit = new RectF(0, 0, 0, 0);
        args.rowsRightToLeft(NavigationArgs.Type.ALL, 2, 2, limit);
        switchPageNavigationMode(args);
    }

    private void switchPageNavigationMode(NavigationArgs args) {
        BaseReaderRequest request = new ChangeLayoutRequest(ReaderConstants.SINGLE_PAGE_NAVIGATION_LIST, args);
        submitRenderRequest(request);
    }

    private void resetNavigationMode() {
        BaseReaderRequest request = new ChangeLayoutRequest(ReaderConstants.SINGLE_PAGE, new NavigationArgs());
        submitRenderRequest(request);
    }

    private void adjustContrast() {
        final AdjustContrastAction action = new AdjustContrastAction();
        action.execute(this);
    }

    private void adjustEmbolden() {
        final EmboldenAction action = new EmboldenAction();
        action.execute(this);
    }

    private void imageReflow() {
        final ImageReflowAction action = new ImageReflowAction();
        action.execute(this);
    }

    public void onDocumentOpened(String path) {
        documentPath = path;
        hideToolbar();
        updateToolbarTitle();
    }

    public void submitRenderRequest(final BaseReaderRequest renderRequest) {
        reader.submitRequest(this, renderRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Exception e) {
                handleRenderRequestFinished(renderRequest, e);
                preRenderNext();
            }
        });
    }

    private void saveReaderViewInfo(final BaseReaderRequest request) {
        Debug.d(TAG, "saveReaderViewInfo: " + JSON.toJSONString(request.getReaderViewInfo().getFirstVisiblePage()));
        readerViewInfo = request.getReaderViewInfo();
    }

    public final ReaderViewInfo getReaderViewInfo() {
        return readerViewInfo;
    }


    private void handleRenderRequestFinished(final BaseReaderRequest request, Exception e) {
        Debug.d(TAG, "handleRenderRequestFinished: " + request + ", " + e);
        if (e != null) {
            return;
        }
        saveReaderViewInfo(request);
        updateToolbarProgress();
//        ReaderDeviceManager.applyGCInvalidate(surfaceView);
        drawPage(reader.getViewportBitmap().getBitmap());
    }

    public void redrawPage() {
        if (reader != null) {
            submitRenderRequest(new RenderRequest());
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
        PageInfo pageInfo = getReaderViewInfo().getFirstVisiblePage();
        List<ReaderSelection> list = getReaderViewInfo().getSearchResults();
        if (list == null || list.size() <= 0) {
            return;
        }
        for (ReaderSelection sel : list) {
            drawHighlightRectangles(canvas, paint, pageInfo.getDisplayRect(), sel.getRectangles());
        }
    }

    private void drawHighlightRectangles(Canvas canvas, Paint paint, final RectF page, List<RectF> rectangles) {
        if (rectangles == null) {
            return;
        }
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setXfermode(xorMode);
        for(int j = 0; j < rectangles.size(); ++j) {
            final RectF rect = new RectF(rectangles.get(j));
            rect.offset(page.left, page.top);
            canvas.drawRect(rect, paint);
        }
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

    private void quitApplication() {

    }

    private void openBuiltInDoc() {
    }

    private boolean hasPopupWindow() {
        return isReaderMenuShown();
    }

    private void hideAllPopupMenu() {
        hideReaderMenu();
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

    private ReaderMenu getReaderMenu() {
        if (readerMenu == null) {
            initReaderMenu();
        }
        return readerMenu;
    }

    public final HandlerManager getHandlerManager() {
        return handlerManager;
    }

    public boolean isReaderMenuShown() {
        return getReaderMenu().isShown();
    }

    public void showReaderMenu() {
        showToolbar();
        getReaderMenu().show();
    }

    public void hideReaderMenu() {
        hideToolbar();
        getReaderMenu().hide();
    }

    private void showToolbar() {
        findViewById(R.id.toolbar_top).setVisibility(View.VISIBLE);
        findViewById(R.id.toolbar_bottom).setVisibility(View.VISIBLE);
    }

    private void hideToolbar() {
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
}