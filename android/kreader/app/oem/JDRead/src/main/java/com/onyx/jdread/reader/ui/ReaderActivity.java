package com.onyx.jdread.reader.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.DeviceUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ActivityReaderBinding;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.model.MainBundle;
import com.onyx.jdread.reader.actions.OpenDocumentAction;
import com.onyx.jdread.reader.actions.ParserOpenDocumentInfoAction;
import com.onyx.jdread.reader.actions.PrevPageAction;
import com.onyx.jdread.reader.common.DocumentInfo;
import com.onyx.jdread.reader.common.ReaderViewBack;
import com.onyx.jdread.reader.data.PageTurningDetector;
import com.onyx.jdread.reader.data.PageTurningDirection;
import com.onyx.jdread.reader.event.ReaderActivityEventHandler;
import com.onyx.jdread.reader.model.ReaderViewModel;
import com.onyx.jdread.reader.model.SelectMenuModel;

/**
 * Created by huxiaomao on 2017/12/7.
 */

public class ReaderActivity extends AppCompatActivity implements ReaderViewBack {
    private ActivityReaderBinding binding;
    private SurfaceHolder.Callback surfaceHolderCallback;
    private ReaderViewModel readerViewModel;
    private SelectMenuModel selectMenuModel;
    private ReaderActivityEventHandler readerActivityEventHandler;
    private boolean isGuide = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        registerListener();
        initData();
    }

    protected void setGuide(boolean isGuide) {
        this.isGuide = isGuide;
    }

    private void initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reader);
        readerViewModel = new ReaderViewModel();
        binding.setReadViewModel(readerViewModel);
        readerActivityEventHandler = new ReaderActivityEventHandler(readerViewModel,this);
        initLastPageView();
        initSurfaceView();
        initSelectMenu();
    }

    private void initSelectMenu(){
        selectMenuModel = new SelectMenuModel(readerViewModel.getReaderDataHolder().getHandlerManger());
        binding.readerPopupSelectionMenu.setSelectMenuModel(selectMenuModel);
        selectMenuModel.setBinding(binding.readerPopupSelectionMenu,readerViewModel.getEventBus());
        readerViewModel.getReaderDataHolder().setSelectMenuModel(selectMenuModel);
    }

    private void initData() {
        if (isGuide) {
            finish();
            return;
        }
        final ParserOpenDocumentInfoAction parserOpenDocumentInfoAction = new ParserOpenDocumentInfoAction(getIntent());
        parserOpenDocumentInfoAction.execute(readerViewModel.getReaderDataHolder(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                if (binding.getReadViewModel().setDocumentInfo(parserOpenDocumentInfoAction.getDocumentInfo())) {
                    updateLoadingState();
                    readerViewModel.setReaderPageView(binding.readerPageView);
                    OpenDocumentAction openDocumentAction = new OpenDocumentAction(ReaderActivity.this);
                    openDocumentAction.execute(readerViewModel.getReaderDataHolder(),null);
                }
            }
        });

    }

    private void updateLoadingState(){
        if(readerViewModel.getReaderDataHolder().isPreload() ){
            setTimeMessage(getTimMessage());
            return;
        }
        if(readerViewModel.getReaderDataHolder().getDocumentInfo().getOpenType() == DocumentInfo.OPEN_BOOK_CATALOG){
            setTimeMessage(getTimMessage());
        }
    }

    private void setTimeMessage(String message){
        readerViewModel.setTipMessage(message);
        readerViewModel.setIsShowTipMessage(true);
    }

    private String getTimMessage(){
        if(readerViewModel.getReaderDataHolder().isPreload()){
            return ResManager.getString(R.string.preload_loading);
        }
        if(readerViewModel.getReaderDataHolder().getDocumentInfo().getOpenType() == DocumentInfo.OPEN_BOOK_CATALOG){
            return ResManager.getString(R.string.catalog_loading);
        }
        return ResManager.getString(R.string.loading);
    }

    private void initLastPageView() {
        binding.buttonBackToLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReaderActivity.this.finish();
            }
        });

        final GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.OnGestureListener() {

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (PrevPageAction.getRegionOne(ReaderActivity.this).contains((int)e.getX(), (int)e.getY())) {
                    readerViewModel.setIsShowLastPage(false);
                }
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                PageTurningDirection direction = PageTurningDetector.detectHorizontalTuring(ReaderActivity.this, (int)(e2.getX() - e1.getX()));
                if (direction == PageTurningDirection.Left) {
                    readerViewModel.setIsShowLastPage(false);
                }
                return true;
            }

        });
        binding.layoutLastPage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
    }

    private void initSurfaceView() {
        binding.readerPageView.requestFocusFromTouch();
    }

    private void addSurfaceViewCallback() {
        binding.readerPageView.getHolder().addCallback(getSurfaceCallback());
    }

    private void removeSurfaceViewCallback() {
        if (surfaceHolderCallback != null) {
            binding.readerPageView.getHolder().removeCallback(surfaceHolderCallback);
        }
    }

    protected SurfaceHolder.Callback getSurfaceCallback() {
        if (surfaceHolderCallback == null) {
            surfaceHolderCallback = new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {
                    readerViewModel.clearSurfaceView(binding.readerPageView);
                    updateView();
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                    surfaceHolder.removeCallback(surfaceHolderCallback);
                    surfaceHolderCallback = null;
                }
            };
        }
        return surfaceHolderCallback;
    }

    private void updateView(){
        if (readerActivityEventHandler != null && readerActivityEventHandler.isLostFocus()) {
            readerActivityEventHandler.setLostFocus(false);
            readerActivityEventHandler.updatePageView();
        }
    }

    private void registerListener() {
        readerActivityEventHandler.registerListener();
    }

    @Override
    protected void onDestroy() {
        readerActivityEventHandler.unregisterListener();
        MainBundle.getInstance().getSystemBarModel().setIsShow(true);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        addSurfaceViewCallback();
        super.onResume();
        DeviceUtils.setFullScreenOnResume(this,true);
        if (readerActivityEventHandler != null) {
            readerActivityEventHandler.updateTimeFormat();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeSurfaceViewCallback();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                finish();
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public FragmentActivity getContext() {
        return this;
    }
}
