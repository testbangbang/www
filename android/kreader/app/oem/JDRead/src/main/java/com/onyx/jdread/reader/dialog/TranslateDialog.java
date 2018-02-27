package com.onyx.jdread.reader.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ActivityTranslateBinding;
import com.onyx.jdread.reader.event.TranslateDialogEventHandler;
import com.onyx.jdread.reader.event.UpdateViewPageEvent;
import com.onyx.jdread.reader.model.TranslateViewModel;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by huxiaomao on 2018/1/22.
 */

public class TranslateDialog extends Dialog implements ViewCallBack {
    private static final String TAG = ReaderNoteDialog.class.getSimpleName();
    private ActivityTranslateBinding binding;
    private TranslateViewModel translateViewModel;
    private String text;
    private EventBus eventBus;
    private TranslateDialogEventHandler handler;
    private float x;
    private float y;

    public TranslateDialog(@NonNull Activity activity, String text, EventBus eventBus, float x, float y) {
        super(activity, android.R.style.Theme_NoTitleBar);
        this.text = text;
        this.eventBus = eventBus;
        this.x = x;
        this.y = y;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.activity_translate, null, false);
        setContentView(binding.getRoot());

        translateViewModel = new TranslateViewModel(eventBus);
        binding.setTranslateViewModel(translateViewModel);
        translateViewModel.setBinding(binding);
        translateViewModel.setViewCallBack(this);

        handler = new TranslateDialogEventHandler(translateViewModel);
        handler.registerListener();
    }

    @Override
    public void show() {
        setDialogParams(this);
        super.show();
    }

    private void initData() {
        translateViewModel.setText(text);
    }

    @Override
    public void dismiss() {
        handler.unregisterListener();
        super.dismiss();
        eventBus.post(new UpdateViewPageEvent());
    }

    @Override
    public Dialog getContent() {
        return this;
    }

    public void setDialogParams(Dialog dialog) {
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
        params.height = (int) getContext().getResources().getDimension(R.dimen.reader_select_menu_translate_height);
        params.width = (int) getContext().getResources().getDimension(R.dimen.reader_select_menu_width);

        params.x = (int) x;
        params.y = (int) y;
        dialogWindow.setBackgroundDrawableResource(R.drawable.rectangle_stroke_corners);
        dialogWindow.setAttributes(params);
    }
}