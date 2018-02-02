package com.onyx.jdread.main.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.onyx.jdread.main.view.PowerOffDialog;

/**
 * Created by hehai on 18-2-2.
 */

public class PowerOffActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PowerOffDialog.Builder builder = new PowerOffDialog.Builder(context);
        builder.create().show();
    }
}
