package com.onyx.jdread.reader.ui;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.SurfaceHolder;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.api.device.epd.UpdateScheme;
import com.onyx.android.sdk.utils.DeviceUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ActivityReaderBinding;
import com.onyx.jdread.main.common.JDPreferenceManager;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.model.MainBundle;
import com.onyx.jdread.reader.actions.OpenDocumentAction;
import com.onyx.jdread.reader.actions.ParserOpenDocumentInfoAction;
import com.onyx.jdread.reader.common.ReaderViewBack;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        registerListener();
        initData();
    }

    private void initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reader);
        readerViewModel = new ReaderViewModel();
        binding.setReadViewModel(readerViewModel);
        readerActivityEventHandler = new ReaderActivityEventHandler(readerViewModel,this);
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
        if (JDPreferenceManager.getBooleanValue(R.string.speed_refresh_key,false)) {
            EpdController.setSystemUpdateModeAndScheme(UpdateMode.ANIMATION, UpdateScheme.QUEUE_AND_MERGE, Integer.MAX_VALUE);
        }
        ParserOpenDocumentInfoAction parserOpenDocumentInfoAction = new ParserOpenDocumentInfoAction(getIntent());
        parserOpenDocumentInfoAction.execute(readerViewModel.getReaderDataHolder(),null);
        if (binding.getReadViewModel().setDocumentInfo(parserOpenDocumentInfoAction.getDocumentInfo())) {
            updateLoadingState();
            readerViewModel.setReaderPageView(binding.readerPageView);
            OpenDocumentAction openDocumentAction = new OpenDocumentAction(this);
            openDocumentAction.execute(readerViewModel.getReaderDataHolder(),null);
        }
    }

    private void updateLoadingState(){
        if(readerViewModel.getReaderDataHolder().isPreload()){
            readerViewModel.setTipMessage(ResManager.getString(R.string.preload_loading));
            readerViewModel.setIsShowTipMessage(true);
        }
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

    private void registerListener() {
        readerActivityEventHandler.registerListener();
    }

    @Override
    protected void onDestroy() {
        if (JDPreferenceManager.getBooleanValue(R.string.speed_refresh_key,false)) {
            EpdController.clearSystemUpdateModeAndScheme();
        }
        readerActivityEventHandler.unregisterListener();
        MainBundle.getInstance().getSystemBarModel().setIsShow(true);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        addSurfaceViewCallback();
        super.onResume();
        DeviceUtils.setFullScreenOnResume(this,true);
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
    public Activity getContext() {
        return this;
    }
}
