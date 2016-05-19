package com.onyx.kreader.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.*;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.LinearLayout;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.onyx.kreader.R;
import com.onyx.kreader.host.wrapper.ReaderManager;
import com.onyx.kreader.ui.actions.*;
import com.onyx.kreader.api.ReaderDocumentOptions;
import com.onyx.kreader.api.ReaderException;
import com.onyx.kreader.api.ReaderPluginOptions;
import com.onyx.kreader.common.BaseCallback;
import com.onyx.kreader.common.BaseRequest;
import com.onyx.kreader.device.DeviceController;
import com.onyx.kreader.host.impl.ReaderDocumentOptionsImpl;
import com.onyx.kreader.host.impl.ReaderPluginOptionsImpl;
import com.onyx.kreader.host.navigation.NavigationArgs;
import com.onyx.kreader.host.options.ReaderConstants;
import com.onyx.kreader.host.request.*;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.ui.data.ReaderScalePresets;
import com.onyx.kreader.ui.gesture.MyOnGestureListener;
import com.onyx.kreader.ui.gesture.MyScaleGestureListener;
import com.onyx.kreader.ui.handler.HandlerManager;
import com.onyx.kreader.ui.menu.ReaderMenu;
import com.onyx.kreader.ui.menu.ReaderMenuItem;
import com.onyx.kreader.ui.menu.ReaderSideMenu;
import com.onyx.kreader.ui.menu.ReaderSideMenuItem;
import com.onyx.kreader.utils.FileUtils;
import com.onyx.kreader.utils.RawResourceUtil;
import com.onyx.kreader.utils.StringUtils;

import java.util.List;

/**
 * Created by Joy on 2016/4/14.
 */
public class ReaderActivity extends Activity {
    private final static String TAG = ReaderActivity.class.getSimpleName();

    private Reader reader;

    private SurfaceView surfaceView;
    private SurfaceHolder.Callback surfaceHolderCallback;
    private SurfaceHolder holder;

    private ReaderMenu readerMenu;

    private HandlerManager handlerManager;
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleDetector;

    private boolean preRender = true;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen(true);
        setContentView(R.layout.activity_reader);
        initActivity();
    }

    @Override
    protected void onResume() {
        redrawPage();
        super.onResume();
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
        if (getReaderMenu().isShown()) {
            getReaderMenu().hide();
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

    public void nextScreen() {
        final NextScreenAction action = new NextScreenAction();
        action.execute(this);
    }

    public void prevScreen() {
        final PreviousScreenAction action = new PreviousScreenAction();
        action.execute(this);
    }

    public void preRenderNext() {
        if (!preRender) {
            return;
        }

        final PrerenderRequest request = new PrerenderRequest(true);
        reader.submitRequest(this, request, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Exception e) {
                if (e != null) {
                    return;
                }
            }
        });
    }

    public void nextPage() {
        nextScreen();
    }

    public void prevPage() {
        prevScreen();
    }

    public void scaleEnd() {

    }

    public void scaleBegin(ScaleGestureDetector detector) {

    }

    public void scaling(ScaleGestureDetector detector) {

    }

    public void panFinished(int offsetX, int offsetY) {

    }

    public void panning(int offsetX, int offsetY) {

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
        initActionBar();
        initSurfaceView();
        initReaderMenu();
        initHandlerManager();
    }

    private void initActionBar() {
        if (getActionBar() != null) {
            getActionBar().setDisplayShowHomeEnabled(true);
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
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

        // make sure we openLocalFile the doc after surface view is layouted correctly.
        surfaceView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                surfaceView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                handleActivityIntent();
            }
        });
    }

    private void initHandlerManager() {
        handlerManager = new HandlerManager(this);
        handlerManager.setEnable(false);
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
                Uri uri = getIntent().getData();
                if (uri == null) {
                    return false;
                }
                openLocalFile(FileUtils.getRealFilePathFromUri(ReaderActivity.this, uri));
            } else if (action.equals(Intent.ACTION_SEARCH)) {
                searchContent(getIntent(), true);
            }
            return true;
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void searchContent(Intent intent, boolean b) {

    }

    private void openLocalFile(final String path) {
        reader = ReaderManager.getReader(path);
        final OpenDocumentAction action = new OpenDocumentAction(path);
        action.execute(this);
    }

    private void gotoPage(int page) {
        final GotoPageAction action = new GotoPageAction(String.valueOf(page));
        action.execute(this);
    }

    private void scaleUp() {
        try {
            float actualScale = reader.getReaderLayoutManager().getActualScale();
            scaleByValue(ReaderScalePresets.scaleUp(actualScale));
        } catch (ReaderException e) {
            e.printStackTrace();
        }
    }

    private void scaleDown() {
        try {
            float actualScale = reader.getReaderLayoutManager().getActualScale();
            scaleByValue(ReaderScalePresets.scaleDown(actualScale));
        } catch (ReaderException e) {
            e.printStackTrace();
        }
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
        BaseRequest request = new ChangeLayoutRequest(ReaderConstants.SINGLE_PAGE_NAVIGATION_LIST, args);
        submitRenderRequest(request);
    }

    private void resetNavigationMode() {
        BaseRequest request = new ChangeLayoutRequest(ReaderConstants.SINGLE_PAGE, new NavigationArgs());
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

    public void submitRenderRequest(BaseRequest request) {
        reader.submitRequest(this, request, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Exception e) {
                handleRenderRequestFinished(request, e);
                if (preRender) {
                    preRenderNext();
                }
            }
        });
    }

    private void handleRenderRequestFinished(BaseRequest request, Exception e) {
        if (e != null) {
            return;
        }
        DeviceController.applyGCInvalidate(surfaceView);
        drawPage(reader.getViewportBitmap().getBitmap());
    }

    public void redrawPage() {
        if (reader != null) {
            submitRenderRequest(new RenderRequest());
        }
    }

    private void drawPage(Bitmap bitmap) {
        Canvas canvas = holder.lockCanvas();
        if (canvas == null) {
            return;
        }
        Paint paint = new Paint();
        drawBackground(canvas, paint);
        if (bitmap != null) {
            drawBitmap(canvas, paint, bitmap);
        }
        holder.unlockCanvasAndPost(canvas);
    }

    private void drawBackground(Canvas canvas, Paint paint) {
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
    }

    private void drawBitmap(Canvas canvas, Paint paint, Bitmap bitmap) {
        canvas.drawBitmap(bitmap, 0, 0, paint);
    }

    public String getCurrentPageName() {
        return reader.getReaderLayoutManager().getCurrentPageName();
    }

    public ReaderPluginOptions getPluginOptions() {
        return new ReaderPluginOptionsImpl();
    }

    public ReaderDocumentOptions getDocumentOptions() {
        return new ReaderDocumentOptionsImpl("", "");
    }

    private void quitApplication() {

    }

    private void openBuiltInDoc() {
    }

    private boolean hasPopupWindow() {
        return getReaderMenu().isShown();
    }

    private void hideAllPopupMenu() {
        getReaderMenu().hide();
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
            // delay init of reader menu to speed up activity startup
            initReaderMenu();
        }
        return readerMenu;
    }

    public final HandlerManager getHandlerManager() {
        return handlerManager;
    }

    public void showReaderMenu() {
        getReaderMenu().show();
    }

    public void setFullScreen(boolean fullScreen) {
        DeviceController.setFullScreen(this, fullScreen);
    }
}