package com.onyx.jdread.reader.ui;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.SurfaceHolder;

import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ActivityReaderBinding;
import com.onyx.jdread.reader.actions.OpenDocumentAction;
import com.onyx.jdread.reader.actions.ParserOpenDocumentInfoAction;
import com.onyx.jdread.reader.common.ReaderViewBack;
import com.onyx.jdread.reader.event.ReaderActivityEventHandler;
import com.onyx.jdread.reader.model.ReaderViewModel;

/**
 * Created by huxiaomao on 2017/12/7.
 */

public class ReaderActivity extends AppCompatActivity implements ReaderViewBack {
    private ActivityReaderBinding binding;
    private SurfaceHolder.Callback surfaceHolderCallback;
    private ReaderViewModel readerViewModel;
    private ReaderActivityEventHandler readerActivityEventHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initThirdLibrary();
        initData();
    }

    private void initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reader);
        readerViewModel = new ReaderViewModel();
        binding.setReadViewModel(readerViewModel);
        readerActivityEventHandler = new ReaderActivityEventHandler(readerViewModel,this);
        initSurfaceView();
    }

    private void initData() {
        ParserOpenDocumentInfoAction parserOpenDocumentInfoAction = new ParserOpenDocumentInfoAction(getIntent());
        parserOpenDocumentInfoAction.execute(readerViewModel.getReaderDataHolder());
        if (binding.getReadViewModel().setDocumentInfo(parserOpenDocumentInfoAction.getDocumentInfo())) {
            readerViewModel.setReaderPageView(binding.readerPageView);
            OpenDocumentAction openDocumentAction = new OpenDocumentAction(readerViewModel.getReaderDataHolder());
            openDocumentAction.execute(readerViewModel.getReaderDataHolder());
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

    private void initThirdLibrary() {
        readerActivityEventHandler.registeredLibrary();
    }

    @Override
    protected void onDestroy() {
        readerActivityEventHandler.unregisteredLibrary();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        addSurfaceViewCallback();
        super.onResume();
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
