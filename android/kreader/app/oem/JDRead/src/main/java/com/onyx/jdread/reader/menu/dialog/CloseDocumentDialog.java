package com.onyx.jdread.reader.menu.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;

import com.onyx.android.sdk.utils.DeviceUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ReaderCloseMenuBinding;
import com.onyx.jdread.main.model.MainBundle;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.dialog.ViewCallBack;
import com.onyx.jdread.reader.menu.model.CloseDialogModel;


/**
 * Created by huxiaomao on 17/5/10.
 */

public class CloseDocumentDialog extends Dialog implements ViewCallBack {
    private static final String TAG = CloseDocumentDialog.class.getSimpleName();
    private ReaderDataHolder readerDataHolder;
    private ReaderCloseMenuBinding binding;

    public CloseDocumentDialog(ReaderDataHolder readerDataHolder, @NonNull Activity activity) {
        super(activity, android.R.style.Theme_Translucent_NoTitleBar);
        this.readerDataHolder = readerDataHolder;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(false);
        initView();
        initData();
    }

    private void initData() {
        MainBundle.getInstance().getSystemBarModel().setIsShow(true);
        binding.readerSettingSystemBar.setSystemBarModel(MainBundle.getInstance().getSystemBarModel());
        binding.setCloseDialogModel(new CloseDialogModel(readerDataHolder.getEventBus(), this));
    }

    @Override
    public void show() {
        super.show();
        DeviceUtils.adjustFullScreenStatus(this.getWindow(), true);
    }

    private void initView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.reader_close_menu, null, false);
        setContentView(binding.getRoot());
    }

    @Override
    public Dialog getContent() {
        return this;
    }
}
