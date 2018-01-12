package com.onyx.jdread.personal.dialog;

import android.app.DialogFragment;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.DialogExportBinding;
import com.onyx.jdread.personal.event.ExportToEmailEvent;
import com.onyx.jdread.personal.event.ExportToImpressionEvent;
import com.onyx.jdread.personal.event.ExportToNativeEvent;
import com.onyx.jdread.util.Utils;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by li on 2018/1/3.
 */

public class ExportDialog extends DialogFragment implements View.OnClickListener {
    private DialogExportBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = (DialogExportBinding) DataBindingUtil.inflate(inflater, R.layout.dialog_export_layout, container, false);
        init();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        int screenWidth = Utils.getScreenWidth(JDReadApplication.getInstance());
        int screenHeight = Utils.getScreenHeight(JDReadApplication.getInstance());
        params.width = (int) (screenWidth * Utils.getValuesFloat(R.integer.export_dialog_width_rate));
        params.height = (int) (screenHeight * Utils.getValuesFloat(R.integer.export_dialog_height_rate));
        window.setAttributes(params);
    }

    private void init() {
        binding.dialogExportClose.setOnClickListener(this);
        binding.dialogExportToNative.setOnClickListener(this);
        binding.dialogExportToEmail.setOnClickListener(this);
        binding.dialogExportToImpression.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_export_close:
                break;
            case R.id.dialog_export_to_native:
                EventBus.getDefault().post(new ExportToNativeEvent());
                break;
            case R.id.dialog_export_to_email:
                EventBus.getDefault().post(new ExportToEmailEvent());
                break;
            case R.id.dialog_export_to_impression:
                EventBus.getDefault().post(new ExportToImpressionEvent());
                break;
        }
        dismiss();
    }
}
