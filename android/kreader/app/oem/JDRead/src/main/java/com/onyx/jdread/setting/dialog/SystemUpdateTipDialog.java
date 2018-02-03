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
import com.onyx.jdread.databinding.DialogUpdateTipBinding;
import com.onyx.jdread.setting.event.SystemPackageDownloadEvent;
import com.onyx.jdread.setting.model.SettingBundle;
import com.onyx.jdread.util.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by li on 2018/2/2.
 */

public class SystemUpdateTipDialog extends DialogFragment {
    private DialogUpdateTipBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);
        binding = (DialogUpdateTipBinding) DataBindingUtil.inflate(inflater, R.layout.dialog_system_update_layout, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.ensureRegister(SettingBundle.getInstance().getEventBus(), this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Utils.ensureUnregister(SettingBundle.getInstance().getEventBus(), this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSystemPackageDownloadEvent(SystemPackageDownloadEvent event) {
        binding.setProgress(event.getProgress());
    }
}
