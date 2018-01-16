package com.onyx.edu.homework.action;

import android.content.Context;
import android.content.DialogInterface;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.ui.dialog.OnyxCustomDialog;
import com.onyx.edu.homework.R;
import com.onyx.edu.homework.base.BaseAction;
import com.onyx.edu.homework.event.ReloadQuestionViewEvent;
import com.onyx.edu.homework.event.StopNoteEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lxm on 2018/1/16.
 */

public class ShowExpiredDialogAction extends BaseAction {

    private EventBus eventBus;

    public ShowExpiredDialogAction(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void execute(Context context, BaseCallback baseCallback) {
        eventBus.post(new StopNoteEvent(false));
        OnyxCustomDialog.getConfirmDialog(context, context.getString(R.string.end_time_tips),
                false,
                null,
                null).addOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                eventBus.post(new ReloadQuestionViewEvent());
            }
        }).show();
    }
}
