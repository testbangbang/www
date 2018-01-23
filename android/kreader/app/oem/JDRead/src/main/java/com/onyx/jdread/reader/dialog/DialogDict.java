package com.onyx.jdread.reader.dialog;


import android.app.Activity;
import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;

import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ActivityDictBinding;
import com.onyx.jdread.reader.model.DictViewModel;

public class DialogDict extends Dialog implements ViewCallBack {
    private static final String TAG = ReaderNoteDialog.class.getSimpleName();
    private ActivityDictBinding binding;
    private DictViewModel dictViewModel;
    private String inputWord;

    public DialogDict(@NonNull Activity activity,String inputWord) {
        super(activity, android.R.style.Theme_NoTitleBar);
        this.inputWord = inputWord;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(false);
        initView();
        initData();
    }

    private void initView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.activity_dict, null, false);
        setContentView(binding.getRoot());

        dictViewModel = new DictViewModel();
        binding.setDictViewModel(dictViewModel);
        dictViewModel.setBinding(binding);
        dictViewModel.setCallBack(this);
    }

    private void initData() {
        dictViewModel.loadUrl(inputWord);
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
