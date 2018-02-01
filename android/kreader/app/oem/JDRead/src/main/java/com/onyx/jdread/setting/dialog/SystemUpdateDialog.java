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
import com.onyx.jdread.databinding.SystemUpdateAlertBinding;
import com.onyx.jdread.setting.event.ExecuteUpdateEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by li on 2017/12/25.
 */

public class SystemUpdateDialog extends DialogFragment {
    private SystemUpdateAlertBinding binding;
    private EventBus eventBus;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);
        binding = (SystemUpdateAlertBinding) DataBindingUtil.inflate(inflater, R.layout.dialog_system_update_alert, container, false);
        init();
        return binding.getRoot();
    }

    private void init() {
        binding.updateAlertCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        binding.updateAlertConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventBus.post(new ExecuteUpdateEvent());
                dismiss();
            }
        });
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }
}
