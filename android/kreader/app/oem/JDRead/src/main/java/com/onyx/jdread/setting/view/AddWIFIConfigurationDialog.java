package com.onyx.jdread.setting.view;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;

import com.onyx.android.sdk.wifi.WifiAdmin;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.DialogAddWifiBinding;
import com.onyx.jdread.library.view.LibraryDeleteDialog;
import com.onyx.jdread.main.common.ResManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * Created by hehai on 18-1-5.
 */

public class AddWIFIConfigurationDialog extends Dialog {
    public AddWIFIConfigurationDialog(@NonNull Context context) {
        super(context);
    }

    public AddWIFIConfigurationDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {
        private Context context;
        private AddWIFIConfigurationDialog.DialogModel model;

        public Builder(Context context, AddWIFIConfigurationDialog.DialogModel model) {
            this.context = context;
            this.model = model;
        }

        public AddWIFIConfigurationDialog create() {
            AddWIFIConfigurationDialog dialog = new AddWIFIConfigurationDialog(context, R.style.CustomDialogStyle);
            DialogAddWifiBinding binding = DataBindingUtil.bind(View.inflate(context, R.layout.dialog_add_wifi, null));
            binding.setDialogModel(model);
            binding.securitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    model.type.set(model.getWifiTypeList().get(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            dialog.setContentView(binding.getRoot());
            return dialog;
        }
    }

    public static class DialogModel extends Observable {
        private List<Integer> wifiTypeList = new ArrayList<>();
        public final ObservableField<String> ssid = new ObservableField<>();
        public final ObservableInt type = new ObservableInt();
        public final ObservableField<String> password = new ObservableField<>();
        private LibraryDeleteDialog.DialogModel.OnClickListener positiveClickLister;
        private LibraryDeleteDialog.DialogModel.OnClickListener negativeClickLister;

        public DialogModel() {
            wifiTypeList.add(WifiAdmin.SECURITY_NONE);
            wifiTypeList.add(WifiAdmin.SECURITY_WEP);
            wifiTypeList.add(WifiAdmin.SECURITY_PSK);
        }

        public final ObservableField<String> positiveText = new ObservableField<>(JDReadApplication.getInstance().getString(R.string.yes));
        public final ObservableField<String> negativeText = new ObservableField<>(JDReadApplication.getInstance().getString(R.string.no));

        public void setPositiveClickLister(LibraryDeleteDialog.DialogModel.OnClickListener positiveClickLister) {
            this.positiveClickLister = positiveClickLister;
        }

        public void setNegativeClickLister(LibraryDeleteDialog.DialogModel.OnClickListener negativeClickLister) {
            this.negativeClickLister = negativeClickLister;
        }

        public void onPositiveClick() {
            positiveClickLister.onClicked();
        }

        public void onNegativeClick() {
            negativeClickLister.onClicked();
        }

        public List<Integer> getWifiTypeList() {
            return wifiTypeList;
        }
    }

    @Override
    public void show() {
        Window window = getWindow();
        window.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.TOP);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.y = ResManager.getInteger(R.integer.add_wifi_dialog_y);
        attributes.x = ResManager.getInteger(R.integer.add_wifi_dialog_x);
        attributes.width = getContext().getResources().getInteger(R.integer.add_wifi_dialog_width);
        attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(attributes);
        window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        super.show();
    }
}
