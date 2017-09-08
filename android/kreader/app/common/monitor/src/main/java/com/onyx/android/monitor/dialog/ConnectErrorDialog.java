package com.onyx.android.monitor.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.onyx.android.monitor.R;
import com.onyx.android.monitor.event.ConnectEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Shows OK/Cancel confirmation dialog about camera permission.
 */
public class ConnectErrorDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Activity activity = getActivity();
        return new AlertDialog.Builder(getActivity())
                .setMessage(R.string.connect_error)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EventBus.getDefault().post(new ConnectEvent());
                    }
                })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                activity.finish();
                            }
                        })
                .create();
    }
}
