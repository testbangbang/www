package com.onyx.jdread.manager;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.onyx.jdread.R;
import com.onyx.jdread.main.activity.LockScreenActivity;
import com.onyx.jdread.main.activity.MainActivity;
import com.onyx.jdread.main.activity.StartActivity;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.reader.ui.PreloadActivity;
import com.onyx.jdread.reader.ui.SettingsActivity;
import com.onyx.jdread.shop.event.MenuWifiSettingEvent;
import com.onyx.jdread.shop.view.CustomDialog;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by hehai on 17-3-14.
 */

public class ManagerActivityUtils {
    private static final String TAG = ManagerActivityUtils.class.getSimpleName();
    public static final String ACTION_MASTER_CLEAR = "android.intent.action.MASTER_CLEAR";

    public static void showWifiDialog(final Context context) {
        CustomDialog.Builder builder = new CustomDialog.Builder(context);
        builder.setTitle(context.getString(R.string.wifi_dialog_title))
                .setMessage(context.getString(R.string.wifi_dialog_content))
                .setPositiveButton(context.getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EventBus.getDefault().post(new MenuWifiSettingEvent(context.getString(R.string.menu_wifi_setting)));
                        dialog.dismiss();
                    }
                }).setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    public static void lockScreen(Context context) {
        Intent intent = new Intent(context, LockScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void lockScreen(Activity context) {
        Intent intent = new Intent(context, LockScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivityForResult(intent, Constants.REQUEST_CODE);
    }

    public static void startMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void startPreloadActivity(Context context) {
        Intent intent = new Intent(context, PreloadActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void startStartActivity(Context context) {
        Intent intent = new Intent(context, StartActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void startProductionTest(Context context) {
        try {
            Intent intent = new Intent();
            String packageName = "com.onyx.android.production.test";
            String className = "com.onyx.android.productiontest.activity.ProductionTestMainActivity";
            intent.setClassName(packageName, className);
            context.startActivity(intent);
        } catch (Exception e) {
        }

    }

    public static void reset(Context context) {
        Intent intent = new Intent();
        intent.setAction(ACTION_MASTER_CLEAR);
        context.sendBroadcast(intent);
    }

    public static void startSettingsActivity(Context context,long ebookId) {
        Intent intent = new Intent(context, SettingsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(ebookId != Integer.MAX_VALUE) {
            intent.putExtra(Constants.SP_KEY_BOOK_ID, ebookId);
        }
        context.startActivity(intent);
    }
}
