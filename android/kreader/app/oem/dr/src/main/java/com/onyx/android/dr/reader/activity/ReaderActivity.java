package com.onyx.android.dr.reader.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.bean.GoodSentenceBean;
import com.onyx.android.dr.bean.NewWordBean;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.manager.OperatingDataManager;
import com.onyx.android.dr.reader.action.ShowQuickPreviewAction;
import com.onyx.android.dr.reader.base.ReaderView;
import com.onyx.android.dr.reader.common.ReadPageInfo;
import com.onyx.android.dr.reader.common.ReaderConstants;
import com.onyx.android.dr.reader.common.ReaderDeviceManager;
import com.onyx.android.dr.reader.common.ToastManage;
import com.onyx.android.dr.reader.data.BookInfo;
import com.onyx.android.dr.reader.data.SingletonSharedPreference;
import com.onyx.android.dr.reader.dialog.DialogSearch;
import com.onyx.android.dr.reader.dialog.ReaderDialogManage;
import com.onyx.android.dr.reader.event.ActivityPauseEvent;
import com.onyx.android.dr.reader.event.ActivityResumeEvent;
import com.onyx.android.dr.reader.event.BookReadRecordUpdateEvent;
import com.onyx.android.dr.reader.event.DisplayStatusBarEvent;
import com.onyx.android.dr.reader.event.DocumentOpenEvent;
import com.onyx.android.dr.reader.event.FinishReaderEvent;
import com.onyx.android.dr.reader.event.GotoPageAndRedrawPageEvent;
import com.onyx.android.dr.reader.event.ManagePostilDialogEvent;
import com.onyx.android.dr.reader.event.NewFileCreatedEvent;
import com.onyx.android.dr.reader.event.PostilManageDialogDismissEvent;
import com.onyx.android.dr.reader.event.ReaderGoodSentenceMenuEvent;
import com.onyx.android.dr.reader.event.ReaderMainMenuTopBackEvent;
import com.onyx.android.dr.reader.event.ReaderMainMenuTopBookStoreEvent;
import com.onyx.android.dr.reader.event.ReaderMainMenuTopBrightnessEvent;
import com.onyx.android.dr.reader.event.ReaderMainMenuTopMoreEvent;
import com.onyx.android.dr.reader.event.ReaderMainMenuTopSearchEvent;
import com.onyx.android.dr.reader.event.ReaderMainMenuTopShelfEvent;
import com.onyx.android.dr.reader.event.ReaderMainMenuTopUserEvent;
import com.onyx.android.dr.reader.event.ReaderMenuMorePressEvent;
import com.onyx.android.dr.reader.event.ReaderWordQueryMenuEvent;
import com.onyx.android.dr.reader.event.RedrawPageEvent;
import com.onyx.android.dr.reader.event.ScreenshotsSucceedEvent;
import com.onyx.android.dr.reader.handler.HandlerManger;
import com.onyx.android.dr.reader.presenter.ReaderPresenter;
import com.onyx.android.dr.reader.ui.ReaderPaint;
import com.onyx.android.dr.reader.ui.gesture.ReaderOnGestureListener;
import com.onyx.android.dr.reader.utils.CustomFileObserver;
import com.onyx.android.dr.reader.utils.ScreenUtil;
import com.onyx.android.dr.reader.view.BookProgressbar;
import com.onyx.android.dr.reader.view.CustomDialog;
import com.onyx.android.dr.receiver.NetworkConnectChangedReceiver;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.DocumentInfo;
import com.onyx.android.sdk.data.model.v2.CloudMetadata;
import com.onyx.android.sdk.data.model.v2.CloudMetadata_Table;
import com.onyx.android.sdk.device.EnvironmentUtil;
import com.onyx.android.sdk.reader.dataprovider.LegacySdkDataUtils;
import com.onyx.android.sdk.reader.host.request.ChangeViewConfigRequest;
import com.onyx.android.sdk.utils.DeviceUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

/**
 * Created by huxiaomao on 17/5/4.
 */

public class ReaderActivity extends Activity implements ReaderView {
    private static final String TAG = ReaderActivity.class.getSimpleName();
    private SurfaceView surfaceView;
    private SurfaceHolder holder;
    private ReaderPresenter readerPresenter = null;
    private GestureDetector gestureDetector;
    private ReaderPaint readerPaint = new ReaderPaint();
    private TextView readProgress;
    private BookProgressbar progressLoading;
    private SurfaceHolder.Callback surfaceHolderCallback;
    private boolean isFluent;
    private final static int INVALID_ID = -1;
    private int mActivePointerId = INVALID_ID;
    private int mSecondaryPointerId = INVALID_ID;
    private float mPrimaryLastX = -1;
    private float mPrimaryLastY = -1;
    private float mSecondaryLastX = -1;
    private float mSecondaryLastY = -1;
    private CustomDialog dialog;
    private CustomFileObserver fileObserver;
    private TextView bookName;
    private NetworkConnectChangedReceiver networkConnectChangedReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        initThirdLibrary();
        initView();
        initListener();
        initData();
        initReceiver();
    }

    private void initListener() {
        readProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readerPresenter.getPageInformation().setCurrentPage(ReadPageInfo.getCurrentPage(readerPresenter));
                new ShowQuickPreviewAction(readerPresenter).execute(readerPresenter, null);
            }
        });
    }

    private boolean getBookInfo() {
        Uri uri = getIntent().getData();
        if (uri == null) {
            return false;
        }

        BookInfo bookInfo = getReaderPresenter().getBookInfo();

        String path = FileUtils.getRealFilePathFromUri(ReaderActivity.this, uri);
        if (!FileUtils.fileExist(path)) {
            return false;
        }
        CloudMetadata metadata = new Select().from(CloudMetadata.class).where(CloudMetadata_Table.nativeAbsolutePath.eq(path)).querySingle();
        if (metadata != null) {
            bookInfo.setLanguage(metadata.getLanguage());
            DocumentInfo documentInfo = DocumentInfo.create(metadata.getAuthorList(), readerPresenter.getReader().getDocumentMd5(), metadata.getName(), metadata.getNativeAbsolutePath(), metadata.getTitle());
            bookInfo.setDocumentInfo(documentInfo);
        }
        bookInfo.setBookPath(path);

        String bookName = getIntent().getStringExtra(ReaderConstants.BOOK_NAME);
        bookInfo.setBookName(bookName);
        String password = getIntent().getStringExtra(ReaderConstants.BOOK_PASSWORD);
        bookInfo.setPassword(password);
        isFluent = getIntent().getBooleanExtra(ReaderConstants.IS_FLUENT, false);
        return true;
    }

    public void initThirdLibrary() {
        EventBus.getDefault().register(this);
    }

    public void initData() {
        if (!getBookInfo()) {
            finish();
            return;
        }
        progressLoading.setVisibility(View.VISIBLE);
        getReaderPresenter().openDocument();
        getReaderPresenter().setFluent(isFluent);
        bookName.setText(getReaderPresenter().getBookInfo().getBookName());
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

    @Override
    public void showThrowable(Throwable throwable) {

    }

    private void initView() {
        readProgress = (TextView) findViewById(R.id.text_view_progress);
        bookName = (TextView) findViewById(R.id.text_view_book_name);
        progressLoading = (BookProgressbar) findViewById(R.id.progress_bar);
        initSurfaceView();
    }

    private void initSurfaceView() {
        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
        holder = surfaceView.getHolder();
        gestureDetector = new GestureDetector(this, new ReaderOnGestureListener(getReaderPresenter()));
        surfaceHolderCallback = new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                clearCanvas(holder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        };
        holder.addCallback(surfaceHolderCallback);
        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (readerPresenter.isDocumentOpened()) {
                    readerPresenter.getHandlerManger().setTouchStartEvent(event);
                    gestureDetector.onTouchEvent(event);
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        readerPresenter.getHandlerManger().onActionUp(event);
                        readerPresenter.getHandlerManger().resetTouchStartPosition();
                    }
                    if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                        readerPresenter.getHandlerManger().onActionCancel(event);
                        readerPresenter.getHandlerManger().resetTouchStartPosition();
                    }

                    getReaderPresenter().getHandlerManger().onTouchEvent(event);
                }
                return true;
            }
        });

        surfaceView.setFocusable(true);
        surfaceView.setFocusableInTouchMode(true);
        surfaceView.requestFocusFromTouch();
    }

    @Override
    public Context getViewContext() {
        return this;
    }

    @Override
    public Context getApplicationContext() {
        return DRApplication.getInstance();
    }

    @Override
    public View getView() {
        return surfaceView;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void updatePage(Bitmap bitmap) {
        Canvas canvas = holder.lockCanvas(new Rect(surfaceView.getLeft(), surfaceView.getTop(),
                surfaceView.getRight(), surfaceView.getBottom()));
        if (canvas == null) {
            return;
        }
        try {
            readerPaint.drawPage(this, canvas, bitmap, readerPresenter.getReaderUserDataInfo(),
                    readerPresenter.getReaderViewInfo(), readerPresenter.getReaderSelectionManager());
            progressLoading.setVisibility(View.GONE);
            progressLoading.setRun(false);
            updatePageProgress();
        } finally {
            holder.unlockCanvasAndPost(canvas);
        }
    }

    public ReaderPresenter getReaderPresenter() {
        if (readerPresenter == null) {
            readerPresenter = new ReaderPresenter(this);
        }
        return readerPresenter;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (readerPresenter.getHandlerManger().onKeyDown(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().post(new ActivityResumeEvent(this));
        syncReaderPainter();
    }

    private void syncReaderPainter() {
        readerPaint.setAnnotationHighlightStyle(SingletonSharedPreference.AnnotationHighlightStyle.Highlight);
    }

    private void updatePageProgress() {
        readProgress.setText(ReadPageInfo.getReadProgress(readerPresenter));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderMainMenuTopShelfEvent(ReaderMainMenuTopShelfEvent event) {
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderMainMenuTopBookStoreEvent(ReaderMainMenuTopBookStoreEvent event) {
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderMainMenuTopUserEvent(ReaderMainMenuTopUserEvent event) {
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderMainMenuTopSearchEvent(ReaderMainMenuTopSearchEvent event) {
        showSearchDialog(event.getSelectionText());
    }

    private void showSearchDialog(String selectionText) {
        Dialog dlg = new DialogSearch(getReaderPresenter(), selectionText);
        dlg.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderMainMenuTopBackEvent(ReaderMainMenuTopBackEvent event) {
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderMainMenuTopBrightnessEvent(ReaderMainMenuTopBrightnessEvent event) {
        // TODO: 17-7-8 brightness
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderMainMenuTopMoreEvent(ReaderMainMenuTopMoreEvent event) {
        // TODO: 17-7-8 menu more
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderMenuMorePressEvent(ReaderMenuMorePressEvent event) {
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDisplayStatusBarEvent(DisplayStatusBarEvent event) {
        DeviceUtils.setFullScreenOnResume(this, event.isDisplay());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGotoPageAndRedrawPageEvent(GotoPageAndRedrawPageEvent event) {
        readerPresenter.getBookOperate().redrawPage();
        readerPresenter.gotoPage(event.getPage());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onManagePostilDialogEvent(ManagePostilDialogEvent event) {
        ReaderDialogManage.onShowPostilMangeDialog(readerPresenter);
        final ChangeViewConfigRequest request = new ChangeViewConfigRequest(readerPresenter.getReaderView().getView().getWidth() / 2, readerPresenter.getReaderView().getView().getHeight() / 2);
        readerPresenter.getReader().submitRequest(readerPresenter.getReaderView().getViewContext(), request, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                readerPresenter.getBookOperate().redrawPage();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPostilManageDialogDismissEvent(PostilManageDialogDismissEvent event) {
        final ChangeViewConfigRequest request = new ChangeViewConfigRequest(readerPresenter.getReaderView().getView().getWidth(), readerPresenter.getReaderView().getView().getHeight());
        readerPresenter.getReader().submitRequest(readerPresenter.getReaderView().getViewContext(), request, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                readerPresenter.getBookOperate().redrawPage();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cleanupReceiver();
        EventBus.getDefault().post(new FinishReaderEvent());
        HandlerManger handlerManger = getReaderPresenter().getHandlerManger();
        handlerManger.handlerList.get(handlerManger.TTS_PROVIDER).onStop();
        readerPresenter.closeRequest();
        EventBus.getDefault().post(new BookReadRecordUpdateEvent());
        if (dialog != null) {
            dialog.dismiss();
        }
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onDocumentOpened(final DocumentOpenEvent event) {
        prepareGCUpdateInterval();
    }

    private void prepareGCUpdateInterval() {
        ReaderDeviceManager.prepareInitialUpdate(LegacySdkDataUtils.getScreenUpdateGCInterval(this, 5));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                int index = event.getActionIndex();
                mActivePointerId = event.getPointerId(index);
                mPrimaryLastX = MotionEventCompat.getX(event, index);
                mPrimaryLastY = MotionEventCompat.getY(event, index);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                index = event.getActionIndex();
                mSecondaryPointerId = event.getPointerId(index);
                mSecondaryLastX = event.getX(index);
                mSecondaryLastY = event.getY(index);
                if (effective(mPrimaryLastY) && effective(mSecondaryLastY)) {
                    if (!new File(ReaderConstants.SCREENSHOT_PATH).exists()) {
                        fileObserver = new CustomFileObserver(EnvironmentUtil.getExternalStorageDirectory().getAbsolutePath());
                    } else if (fileObserver == null || !fileObserver.getPath().equals(ReaderConstants.SCREENSHOT_PATH)) {
                        fileObserver = new CustomFileObserver(ReaderConstants.SCREENSHOT_PATH);
                    }
                    fileObserver.startWatching();
                    ScreenUtil.screenshot(this);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mActivePointerId = INVALID_ID;
                mPrimaryLastX = -1;
                mPrimaryLastY = -1;
                break;
        }
        return true;
    }

    private boolean effective(float height) {
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        float scale = getResources().getDisplayMetrics().density;
        WindowManager windowManager = getWindowManager();
        int windowHeight = windowManager.getDefaultDisplay().getHeight() + statusBarHeight;
        float effectiveHeight = ReaderConstants.SCREENSHOT_HEIGHT / scale + statusBarHeight * scale;
        if (height < effectiveHeight || height > windowHeight - effectiveHeight) {
            return true;
        }
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScreenshotsSucceedEvent(final ScreenshotsSucceedEvent event) {
        if (dialog == null) {
            CustomDialog.Builder builder = new CustomDialog.Builder(this);
            builder.setTitle(getString(R.string.screen_success));
            dialog = builder.setMessage(String.format(getString(R.string.screen_success_prompt), ReaderConstants.SCREENSHOT_PATH)).setNegativeButton(getString(R.string.close), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create();
        }
        dialog.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewFileCreatedEvent(final NewFileCreatedEvent event) {
        fileObserver.stopWatching();
        if (dialog == null) {
            CustomDialog.Builder builder = new CustomDialog.Builder(this);
            builder.setTitle(getString(R.string.screen_success));
            dialog = builder.setMessage(String.format(getString(R.string.screen_success_prompt), ReaderConstants.SCREENSHOT_PATH)).setNegativeButton(getString(R.string.close), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create();
        }
        dialog.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRedrawPageEvent(RedrawPageEvent event) {
        readerPresenter.getBookOperate().redrawPage();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnReaderGoodSentenceMenuEvent(ReaderGoodSentenceMenuEvent event) {
        addGoodSentence();
    }

    private void addGoodSentence() {
        String selectionText = readerPresenter.getBookOperate().getSelectionText();
        if (StringUtils.isNotBlank(selectionText)) {
            GoodSentenceBean bean = new GoodSentenceBean();
            bean.setDetails(selectionText);
            bean.setReadingMatter(readerPresenter.getBookInfo().getBookName());
            bean.setPageNumber(String.valueOf(readerPresenter.getReaderViewInfo().getFirstVisiblePage().getName()));
            bean.setGoodSentenceType(getGoodSentenceType(readerPresenter.getBookInfo().getLanguage()));
            OperatingDataManager.getInstance().insertGoodSentence(bean);
        } else {
            ToastManage.showMessage(this, getString(R.string.Please_press_to_select_the_sentence_you_want_to_include));
        }
        readerPresenter.getBookOperate().redrawPage();
        readerPresenter.getHandlerManger().updateActionProviderType(HandlerManger.READING_PROVIDER);
        readerPresenter.getReaderSelectionManager().clear();
    }

    private int getGoodSentenceType(String language) {
        int type;
        if (StringUtils.isNotBlank(language)) {
            switch (language) {
                case Constants.CHINESE:
                    type = Constants.CHINESE_TYPE;
                    break;
                case Constants.ENGLISH:
                    type = Constants.ENGLISH_TYPE;
                    break;
                default:
                    type = Constants.OTHER_TYPE;
            }
        } else {
            type = Constants.OTHER_TYPE;
        }
        return type;
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().post(new ActivityPauseEvent(this));
    }

    private void initReceiver() {
        networkConnectChangedReceiver = new NetworkConnectChangedReceiver(new NetworkConnectChangedReceiver.NetworkChangedListener() {
            @Override
            public void onNetworkChanged(boolean connected, int networkType) {
                readerPresenter.onNetworkChanged(connected, networkType);
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
}
