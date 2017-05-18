package com.onyx.android.note.dialog;

import android.app.FragmentManager;
import android.os.Bundle;

import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;

/**
 * Created by solskjaer49 on 16/7/4 17:44.
 */

public class DialogLoading extends OnyxAlertDialog {
    static final public String ARGS_LOADING_MSG = "args_load_msg";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        final String loading_message = getArguments().getString(ARGS_LOADING_MSG);
        setParams(new OnyxAlertDialog.Params().setAlertMsgString(loading_message)
                .setEnableFunctionPanel(false)
                .setEnableTittle(false)
                .setCanceledOnTouchOutside(false)
        );
        super.onCreate(savedInstanceState);
    }

    public void show(FragmentManager fm) {
        super.show(fm, DialogCreateNewFolder.class.getSimpleName());
    }
}
