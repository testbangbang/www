package com.onyx.jdread.setting.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.DialogAssociatedEmailLayoutBinding;
import com.onyx.jdread.main.common.JDPreferenceManager;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.main.util.RegularUtil;
import com.onyx.jdread.util.Utils;

import java.util.Observable;


/**
 * Created by 12 on 2016/12/14.
 */

public class AssociatedEmailDialog extends Dialog {
    private AssociatedEmailDialog(Context context) {
        super(context);
    }

    private AssociatedEmailDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {
        private Context context;
        private DialogModel model;

        public Builder(Context context, DialogModel model) {
            this.context = context;
            this.model = model;
        }

        public AssociatedEmailDialog create() {
            final AssociatedEmailDialog dialog = new AssociatedEmailDialog(context, R.style.CustomDialogStyle);
            DialogAssociatedEmailLayoutBinding bind = DataBindingUtil.bind(View.inflate(context, R.layout.dialog_associated_email_layout, null));
            bind.setDialogModel(model);
            bind.closeMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.setContentView(bind.getRoot());
            return dialog;
        }
    }

    public static class DialogModel extends Observable {
        public final ObservableField<String> title = new ObservableField<>();
        public final ObservableField<String> emailAddress = new ObservableField<>();
        public final ObservableBoolean bound = new ObservableBoolean();

        private boolean checkedEdit() {
            if (StringUtils.isNullOrEmpty(emailAddress.get()) || !RegularUtil.isEmail(emailAddress.get())) {
                ToastUtil.showOffsetToast(ResManager.getString(R.string.email_address_wrong), ResManager.getInteger(R.integer.associated_toast_offset_y));
                return false;
            }
            return true;
        }

        public void bindToEmail() {
            if (checkedEdit()) {
                bound.set(true);
                JDPreferenceManager.setStringValue(R.string.email_address_key, emailAddress.get());
                title.set(JDReadApplication.getInstance().getString(R.string.email_bound));
            }
        }

        public void unbindToEmail() {
            emailAddress.set(null);
            bound.set(false);
            JDPreferenceManager.setStringValue(R.string.email_address_key, null);
            title.set(JDReadApplication.getInstance().getString(R.string.bind_to_email));
        }
    }

    @Override
    public void show() {
        Window window = getWindow();
        window.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = getContext().getResources().getInteger(R.integer.move_to_library_list_dialog_width);
        attributes.height = JDReadApplication.getInstance().getResources().getInteger(R.integer.dialog_associated_width);
        attributes.y = ResManager.getInteger(R.integer.associated_email_dialog_offset_y);
        attributes.x = ResManager.getInteger(R.integer.associated_email_dialog_offset_x);
        window.setAttributes(attributes);
        window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        super.show();
    }
}