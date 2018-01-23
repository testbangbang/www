package com.onyx.jdread.reader.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;

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
        setCanceledOnTouchOutside(false);
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
}