package com.onyx.jdread.main.view;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.onyx.android.sdk.device.Device;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.DialogPowerOffBinding;

/**
 * Created by hehai on 18-2-2.
 */

public class PowerOffDialog extends Dialog {
    private PowerOffDialog(Context context) {
        super(context);
    }

    private PowerOffDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {
        private Context context;

        public Builder(Context context) {
            this.context = context;
        }

        public PowerOffDialog create() {
            final PowerOffDialog dialog = new PowerOffDialog(context, R.style.CustomDialogStyle);
            DialogPowerOffBinding bind = DataBindingUtil.bind(View.inflate(context, R.layout.dialog_power_off, null));
            bind.cancelPowerOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            bind.confirmPowerOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Device.currentDevice.shutdown();
                }
            });
            dialog.setContentView(bind.getRoot());
            return dialog;
        }
    }

    @Override
    public void show() {
        Window window = getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = getContext().getResources().getInteger(R.integer.library_delete_dialog_width);
        attributes.height = getContext().getResources().getInteger(R.integer.library_delete_dialog_height);
        window.setAttributes(attributes);
        window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        super.show();
    }
}
