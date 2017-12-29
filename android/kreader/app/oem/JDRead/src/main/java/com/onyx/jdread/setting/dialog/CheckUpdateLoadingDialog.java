package com.onyx.jdread.setting.dialog;

import android.app.DialogFragment;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.onyx.jdread.R;
import com.onyx.jdread.databinding.UpdateLoadingBinding;

/**
 * Created by li on 2017/12/26.
 */

public class CheckUpdateLoadingDialog extends DialogFragment {
    private UpdateLoadingBinding binding;
    private String tips;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);
        binding = (UpdateLoadingBinding) DataBindingUtil.inflate(inflater, R.layout.update_loading_layout, container, false);
        init();
        return binding.getRoot();
    }

    private void init() {
        binding.setTips(tips);
    }

    public void setTips(String tips) {
        this.tips = tips;
        if (binding != null) {
            binding.setTips(tips);
        }
    }
}
