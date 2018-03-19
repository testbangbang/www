package com.onyx.jdread.reader.dialog;


import android.app.Activity;
import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;

import com.onyx.android.sdk.utils.DeviceUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ActivityDictBinding;
import com.onyx.jdread.reader.event.UpdateViewPageEvent;
import com.onyx.jdread.reader.model.DictViewModel;

import org.greenrobot.eventbus.EventBus;

public class DialogBaiduBaiKe extends ReaderBaseDialog implements ViewCallBack {
    private static final String TAG = ReaderNoteDialog.class.getSimpleName();
    private ActivityDictBinding binding;
    private DictViewModel dictViewModel;
    private String inputWord;
    private EventBus eventBus;

    public DialogBaiduBaiKe(@NonNull Activity activity, String inputWord, EventBus eventBus) {
        super(activity, android.R.style.Theme_NoTitleBar);
        this.inputWord = inputWord;
        this.eventBus = eventBus;
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
        eventBus.post(new UpdateViewPageEvent());
    }

    @Override
    public Dialog getContent() {
        return this;
    }

    @Override
    public void show() {
        super.show();
        DeviceUtils.adjustFullScreenStatus(this.getWindow(),true);
    }
}
