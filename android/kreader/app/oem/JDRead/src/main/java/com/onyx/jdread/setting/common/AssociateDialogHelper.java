package com.onyx.jdread.setting.common;

import android.app.Activity;
import android.content.DialogInterface;

import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.JDPreferenceManager;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.setting.view.AssociatedEmailDialog;
import com.onyx.jdread.util.Utils;

/**
 * Created by li on 2018/3/7.
 */

public class AssociateDialogHelper {
    private static AssociatedEmailDialog dialog;

    public static void showBindEmailDialog(AssociatedEmailDialog.DialogModel model, final Activity context) {
        String email = JDPreferenceManager.getStringValue(R.string.email_address_key, null);
        boolean bound = StringUtils.isNotBlank(email);
        model.title.set(bound ? ResManager.getString(R.string.unbind_to_email) : ResManager.getString(R.string.bind_to_email));
        model.emailAddress.set(email);
        model.bound.set(bound);
        AssociatedEmailDialog.Builder builder = new AssociatedEmailDialog.Builder(context, model);
        dialog = builder.create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Utils.hideSoftWindow(context);
            }
        });
        dialog.show();
    }

    public static void dismissEmailDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
