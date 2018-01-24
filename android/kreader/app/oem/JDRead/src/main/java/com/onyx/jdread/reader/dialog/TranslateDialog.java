package com.onyx.jdread.reader.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ActivityTranslateBinding;
import com.onyx.jdread.reader.model.TranslateViewModel;

/**
 * Created by huxiaomao on 2018/1/22.
 */

public class TranslateDialog extends Dialog implements ViewCallBack {
    private static final String TAG = ReaderNoteDialog.class.getSimpleName();
    private ActivityTranslateBinding binding;
    private TranslateViewModel translateViewModel;
    private String text;

    public TranslateDialog(@NonNull Activity activity, String text) {
        super(activity, android.R.style.Theme_NoTitleBar);
        this.text = text;
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

        translateViewModel = new TranslateViewModel();
        binding.setTranslateViewModel(translateViewModel);
        translateViewModel.setBinding(binding);
        translateViewModel.setViewCallBack(this);
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
        super.dismiss();
    }

    @Override
    public Dialog getContent() {
        return this;
    }

    public void setDialogParams(Dialog dialog){
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager manager = dialogWindow.getWindowManager();
        Display display = manager.getDefaultDisplay();
        params.height = (int)(display.getHeight() * 0.5f);
        params.width = (int)(display.getWidth() * 0.9f);
        params.x = (display.getWidth() - params.width) / 2;
        final float marginBottom = getContext().getResources().getDimension(R.dimen.reader_page_translate_window_margin_bottom);
        params.y = (int)(display.getHeight() - params.height - marginBottom);
        dialogWindow.setAttributes(params);
    }
}