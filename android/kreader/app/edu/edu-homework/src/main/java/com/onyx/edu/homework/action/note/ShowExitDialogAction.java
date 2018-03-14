package com.onyx.edu.homework.action.note;

import android.content.Context;
import android.content.DialogInterface;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.ui.dialog.OnyxCustomDialog;
import com.onyx.edu.homework.DataBundle;
import com.onyx.edu.homework.R;
import com.onyx.edu.homework.base.BaseAction;
import com.onyx.edu.homework.event.ExitEvent;
import com.onyx.edu.homework.event.ResumeNoteEvent;

/**
 * Created by lxm on 2018/1/5.
 */

public class ShowExitDialogAction extends BaseAction {

    @Override
    public void execute(Context context, final BaseCallback baseCallback) {
        OnyxCustomDialog.getConfirmDialog(context, context.getString(R.string.exit_tips), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DataBundle.getInstance().post(new ExitEvent());
                System.exit(0);
            }
        }, null).addOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                DataBundle.getInstance().post(new ResumeNoteEvent());
            }
        }).show();
    }
}
