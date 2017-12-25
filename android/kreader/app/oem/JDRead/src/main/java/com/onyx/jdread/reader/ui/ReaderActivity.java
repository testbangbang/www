package com.onyx.jdread.reader.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;

import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ActivityReaderBinding;
import com.onyx.jdread.reader.actions.CreatePageViewAction;
import com.onyx.jdread.reader.actions.OpenDocumentAction;
import com.onyx.jdread.reader.actions.ParserOpenDocumentInfoAction;
import com.onyx.jdread.reader.event.OpenDocumentFailResultEvent;
import com.onyx.jdread.reader.event.OpenDocumentSuccessResultEvent;
import com.onyx.jdread.reader.model.ReaderViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by huxiaomao on 2017/12/7.
 */

public class ReaderActivity extends AppCompatActivity {
    private ActivityReaderBinding binding;
    private SurfaceHolder.Callback surfaceHolderCallback;
    private ReaderViewModel readerViewModel;

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
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOpenDocumentFailResultEvent(OpenDocumentFailResultEvent event) {
        binding.getReadViewModel().setTipMessage(event.getMessage());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOpenDocumentSuccessResultEvent(OpenDocumentSuccessResultEvent event) {
        CreatePageViewAction createPageViewAction = new CreatePageViewAction();
        createPageViewAction.execute(readerViewModel.getReaderDataHolder());
    }
}
