package com.onyx.jdread.setting.dialog;

import android.app.DialogFragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.onyx.jdread.R;
import com.onyx.jdread.databinding.LoadingDialogLayoutBinding;
import com.onyx.jdread.main.common.LoadingDialog;

/**
 * Created by li on 2017/12/26.
 */

public class CheckUpdateLoadingDialog extends DialogFragment {
    private LoadingDialogLayoutBinding binding;
    private LoadingDialog.DialogModel dialogModel = new LoadingDialog.DialogModel();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.CustomDialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().setCanceledOnTouchOutside(false);
        binding = DataBindingUtil.bind(inflater.inflate(R.layout.loading_dialog_layout, null));
        binding.setLoadingModel(dialogModel);
        binding.imageLoading.setImageResource(R.drawable.loading_animation);
        return binding.getRoot();
    }

    public void setTips(String tips) {
        dialogModel.setLoadingText(tips);
    }
}
