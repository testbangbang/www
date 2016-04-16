package com.onyx.kreader.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.*;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.LinearLayout;
import com.onyx.kreader.R;
import com.onyx.kreader.api.ReaderDocumentOptions;
import com.onyx.kreader.api.ReaderPluginOptions;
import com.onyx.kreader.common.BaseCallback;
import com.onyx.kreader.common.BaseRequest;
import com.onyx.kreader.host.impl.ReaderDocumentOptionsImpl;
import com.onyx.kreader.host.impl.ReaderPluginOptionsImpl;
import com.onyx.kreader.host.request.*;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.host.wrapper.ReaderManager;
import com.onyx.kreader.ui.handler.HandlerManager;
import com.onyx.kreader.utils.FileUtils;
import com.onyx.kreader.utils.StringUtils;
import yalantis.com.sidemenu.interfaces.Resourceble;
import yalantis.com.sidemenu.interfaces.ScreenShotable;
import yalantis.com.sidemenu.model.SlideMenuItem;
import yalantis.com.sidemenu.util.ViewAnimator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joy on 2016/4/14.
 */
public class ReaderActivity extends Activity {
    private final static String TAG = ReaderActivity.class.getSimpleName();

    class MyOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return ReaderActivity.this.handlerManager.onSingleTapUp(ReaderActivity.this, e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            ReaderActivity.this.handlerManager.onLongPress(ReaderActivity.this, e);
        }

        // http://stackoverflow.com/questions/3081711/android-view-gesturedetector-ongesturelistener-onfling-vs-onscroll
        // keep on called
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return handlerManager.onScroll(ReaderActivity.this, e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return handlerManager.onFling(ReaderActivity.this, e1, e2, velocityX, velocityY);
        }

        @Override
        public void onShowPress(MotionEvent e) {
            handlerManager.onShowPress(ReaderActivity.this, e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return handlerManager.onDown(ReaderActivity.this, e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return handlerManager.onDoubleTap(ReaderActivity.this, e);
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return handlerManager.onSingleTapConfirmed(ReaderActivity.this, e);
        }
    }

    class MyScaleGestureListener implements ScaleGestureDetector.OnScaleGestureListener {
        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            ReaderActivity.this.handlerManager.onScaleEnd(ReaderActivity.this, detector);
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return ReaderActivity.this.handlerManager.onScaleBegin(ReaderActivity.this, detector);
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return ReaderActivity.this.handlerManager.onScale(ReaderActivity.this, detector);
        }
    }

    private Reader reader;

    private SurfaceView surfaceView;
    private SurfaceHolder.Callback surfaceHolderCallback;
    private SurfaceHolder holder;

    private LinearLayout readerMenuLayout;
    private ViewAnimator readerMenuViewAnimator;
    private List<SlideMenuItem> readerMenuItemList = new ArrayList<>();

    private HandlerManager handlerManager;
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleDetector;

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
        if (isReaderMenuShown()) {
            hideReaderMenu();
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

    public void nextScreen() {
        final NextScreenRequest renderRequest = new NextScreenRequest();
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

    public void previousPage() {
        previousScreen();
    }

    public boolean isReaderMenuShown() {
        return readerMenuLayout.getVisibility() == View.VISIBLE;
    }

    public void showReaderMenu() {
        if (!isReaderMenuShown()) {
            readerMenuLayout.setVisibility(View.VISIBLE);
            readerMenuViewAnimator.showMenuContent();
        }
    }

    public void hideReaderMenu() {
        if (isReaderMenuShown()) {
            readerMenuLayout.setVisibility(View.INVISIBLE);
            readerMenuViewAnimator.hideMenuContent();
        }
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
        gestureDetector = new GestureDetector(this, new MyOnGestureListener());
        scaleDetector = new ScaleGestureDetector(this, new MyScaleGestureListener());
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
        readerMenuLayout = (LinearLayout)findViewById(R.id.left_drawer);

        createMenuList();
        readerMenuViewAnimator = new ViewAnimator(this, readerMenuItemList, null, null, new ViewAnimator.ViewAnimatorListener() {
            @Override
            public ScreenShotable onSwitch(Resourceble slideMenuItem, ScreenShotable screenShotable, int position) {
                handleMenuItemClicked(slideMenuItem);
                return null;
            }

            @Override
            public void disableHomeButton() {

            }

            @Override
            public void enableHomeButton() {

            }

            @Override
            public void clearContainer() {
                readerMenuLayout.removeAllViews();
            }

            @Override
            public void addViewToContainer(View view) {
                readerMenuLayout.addView(view);
            }
        });
    }

    private void createMenuList() {
        readerMenuItemList.add(new SlideMenuItem("Close", R.drawable.menu_close));
        readerMenuItemList.add(new SlideMenuItem("Style", R.drawable.menu_style));
        readerMenuItemList.add(new SlideMenuItem("Rotation", R.drawable.menu_rotation));
        readerMenuItemList.add(new SlideMenuItem("Zoom", R.drawable.menu_zoom));
        readerMenuItemList.add(new SlideMenuItem("Navigation", R.drawable.menu_navigation));
        readerMenuItemList.add(new SlideMenuItem("Spacing", R.drawable.menu_spacing));
        readerMenuItemList.add(new SlideMenuItem("Font", R.drawable.menu_font));
        readerMenuItemList.add(new SlideMenuItem("Directory", R.drawable.menu_directory));
        readerMenuItemList.add(new SlideMenuItem("More", R.drawable.menu_more));
    }

    private void handleMenuItemClicked(Resourceble slideMenuItem) {
        switch (slideMenuItem.getName()) {
            case "Close":
                hideReaderMenu();
                break;
            default:
                break;
        }
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

    private void scaleToPage() {
        final ScaleToPageRequest renderRequest = new ScaleToPageRequest(getCurrentPageName());
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
}