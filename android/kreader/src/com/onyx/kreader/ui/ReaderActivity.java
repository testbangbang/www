package com.onyx.kreader.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import com.onyx.kreader.api.ReaderDocumentOptions;
import com.onyx.kreader.api.ReaderException;
import com.onyx.kreader.api.ReaderPluginOptions;
import com.onyx.kreader.common.BaseCallback;
import com.onyx.kreader.common.BaseRequest;
import com.onyx.kreader.host.impl.ReaderDocumentOptionsImpl;
import com.onyx.kreader.host.impl.ReaderPluginOptionsImpl;
import com.onyx.kreader.host.navigation.NavigationArgs;
import com.onyx.kreader.host.options.ReaderConstants;
import com.onyx.kreader.host.request.*;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.host.wrapper.ReaderManager;
import com.onyx.kreader.ui.data.ReaderScalePresets;
import com.onyx.kreader.ui.handler.HandlerManager;
import com.onyx.kreader.ui.menu.ReaderMenu;
import com.onyx.kreader.ui.menu.ReaderMenuItem;
import com.onyx.kreader.ui.menu.ReaderSideMenu;
import com.onyx.kreader.ui.menu.ReaderSideMenuItem;
import com.onyx.kreader.utils.FileUtils;
import com.onyx.kreader.utils.RawResourceUtil;
import com.onyx.kreader.utils.StringUtils;
import com.onyx.kreader.ui.gesture.*;

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

    private boolean preRender = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        initActivity();
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
        if (readerMenu.isShown()) {
            readerMenu.hide();
            return true;
        }
        return false;
    }

    public int displayWidth() {
        return surfaceView.getWidth();
    }

    public int displayHeight() {
        return surfaceView.getHeight();
    }

    public void beforePageChangeByUser() {
    }

    public void nextScreen() {
        final NextScreenRequest renderRequest = new NextScreenRequest();
        reader.submitRequest(this, renderRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Exception e) {
                if (e != null) {
                    return;
                }
                preRenderNext();
                drawPage(request.getRenderBitmap().getBitmap());
            }
        });
    }

    public void prevScreen() {
        final PreviousScreenRequest renderRequest = new PreviousScreenRequest();
        reader.submitRequest(this, renderRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Exception e) {
                if (e != null) {
                    return;
                }
                drawPage(request.getRenderBitmap().getBitmap());
            }
        });
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

    public void previousScreen() {
        final PreviousScreenRequest renderRequest = new PreviousScreenRequest();
        reader.submitRequest(this, renderRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Exception e) {
                if (e != null) {
                    return;
                }
                drawPage(request.getRenderBitmap().getBitmap());
            }
        });
    }

    public void nextPage() {
        nextScreen();
    }

    public void prevPage() {
        prevScreen();
    }

    public void previousPage() {
        previousScreen();
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

    private void initActivity() {
        initSurfaceView();
        initReaderMenu();
        initHandlerManager();
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
                        break;
                    case "/Font/Embolden":
                        break;
                    case "/Font/FontReflow":
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
                }
            }
        });
        List<ReaderSideMenuItem> items = createReaderSideMenuItems();
        readerMenu.fillItems(items);
    }

    private void rotateScreen(int rotationOperation) {
        int orientation = computeNewRotation(getRequestedOrientation(), rotationOperation);
        setRequestedOrientation(orientation);
    }

    private int computeNewRotation(int currentOrientation, int rotationOperation) {
        switch (rotationOperation) {
            case 0:
                return currentOrientation;
            case 90:
                if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    return ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                } else if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                } else if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
                    return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                } else if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                    return ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                } else {
                    return ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                }
            case 180:
                if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    return ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                } else if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    return ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                } else if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
                    return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                } else if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                    return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                } else {
                    return ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                }
            case 270:
                if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                } else if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    return ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                } else if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
                    return ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                } else if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                    return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                } else {
                    return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                }
            default:
                assert(false);
                return currentOrientation;
        }
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

    private void openLocalFile(String path) {
        reader = ReaderManager.getReader(path);
        BaseRequest open = new OpenRequest(path, getDocumentOptions(), getPluginOptions());
        reader.submitRequest(this, open, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Exception e) {
                if (e != null) {
                    return;
                }
                onFileOpenSucceed();
            }
        });
    }

    private void onFileOpenSucceed() {
        handlerManager.setEnable(true);
        BaseRequest config = new CreateViewRequest(displayWidth(), displayHeight());
        reader.submitRequest(this, config, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Exception e) {
                if (e != null) {
                    return;
                }
                gotoPage(0);
            }
        });
    }

    private void gotoPage(int page) {
        gotoPage(String.valueOf(page));
    }

    private void gotoPage(String pageName) {
        BaseRequest gotoPosition = new GotoLocationRequest(pageName);
        reader.submitRequest(this, gotoPosition, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Exception e) {
                assert(e == null);
                scaleToPage();
            }
        });
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
        final ScaleByValueRequest renderRequest = new ScaleByValueRequest(getCurrentPageName(), scale);
        submitRenderRequest(renderRequest);
    }

    private void scaleToPage() {
        final ScaleToPageRequest renderRequest = new ScaleToPageRequest(getCurrentPageName());
        submitRenderRequest(renderRequest);
    }

    private void scaleToWidth() {
        final ScaleToWidthRequest renderRequest = new ScaleToWidthRequest(getCurrentPageName());
        submitRenderRequest(renderRequest);
    }

    private void scaleByRect(RectF rect) {
        final ScaleByRectRequest renderRequest = new ScaleByRectRequest(getCurrentPageName(), rect);
        submitRenderRequest(renderRequest);
    }

    private void switchNavigationToArticleMode() {
        NavigationArgs args = new NavigationArgs();
        RectF limit = new RectF(0, 0, 0, 0);
        args.columnsLeftToRight(NavigationArgs.Type.ALL, 2, 2, limit);
        switchNavigationMode(args);
    }

    private void switchNavigationToComicMode() {
        NavigationArgs args = new NavigationArgs();
        RectF limit = new RectF(0, 0, 0, 0);
        args.rowsRightToLeft(NavigationArgs.Type.ALL, 2, 2, limit);
        switchNavigationMode(args);
    }

    private void switchNavigationMode(NavigationArgs args) {
        BaseRequest request = new ChangeLayoutRequest(ReaderConstants.SINGLE_PAGE_NAVIGATION_LIST, args);
        submitRenderRequest(request);
    }

    private void resetNavigationMode() {
        BaseRequest request = new ChangeLayoutRequest(ReaderConstants.SINGLE_PAGE, new NavigationArgs());
        submitRenderRequest(request);
    }

    private void submitRenderRequest(BaseRequest request) {
        reader.submitRequest(this, request, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Exception e) {
                handleRenderRequestFinished(request, e);
            }
        });
    }

    private void handleRenderRequestFinished(BaseRequest request, Exception e) {
        if (e != null) {
            return;
        }
        drawPage(request.getRenderBitmap().getBitmap());
    }

    private void drawPage(Bitmap bitmap) {
        Canvas canvas = holder.lockCanvas();
        if (canvas == null) {
            return;
        }
        Paint paint = new Paint();
        drawBackground(canvas, paint);
        drawBitmap(canvas, paint, bitmap);
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

    private String getCurrentPageName() {
        return reader.getReaderLayoutManager().getCurrentPageName();
    }

    private ReaderPluginOptions getPluginOptions() {
        return new ReaderPluginOptionsImpl();
    }

    private ReaderDocumentOptions getDocumentOptions() {
        return new ReaderDocumentOptionsImpl("", "");
    }

    private void quitApplication() {

    }

    private void openBuiltInDoc() {
        hideLoadingDialog();
    }

    private void hideLoadingDialog() {

    }

    private boolean hasPopupWindow() {
        return readerMenu.isShown();
    }

    private void hideAllPopupMenu() {
        readerMenu.hide();
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

    public void showReaderMenu() {
        readerMenu.show();
    }
}