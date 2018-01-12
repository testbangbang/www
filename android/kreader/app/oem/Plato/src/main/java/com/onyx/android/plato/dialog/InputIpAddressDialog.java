package com.onyx.android.plato.dialog;

import android.app.DialogFragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.onyx.android.plato.R;
import com.onyx.android.plato.common.CloudApiContext;
import com.onyx.android.plato.common.CommonNotices;
import com.onyx.android.plato.databinding.InputIpAddressBinding;
import com.onyx.android.plato.utils.StringUtil;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by zhouzhiming on 2017/12/27.
 */
public class InputIpAddressDialog extends DialogFragment {
    private InputIpAddressBinding binding;
    private EventBus eventBus;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = (InputIpAddressBinding) DataBindingUtil.inflate(inflater, R.layout.dialog_input_ip_address, container, false);
        initEvent();
        return binding.getRoot();
    }

    private void initEvent() {
        binding.inputIpDialogConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = binding.inputIpDialogContent.getText().toString();
                if (StringUtil.isNullOrEmpty(ip)) {
                    CommonNotices.show(getString(R.string.input_ip_address_hint));
                } else {
                    CloudApiContext.BASE_URL = ip;
                    CommonNotices.show(getString(R.string.saved_successfully));
                    dismiss();
                }
            }
        });
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }
}
