package com.onyx.android.sun.common;

import android.content.Context;
import android.content.Intent;

import com.onyx.android.sun.activity.LoginActivity;
import com.onyx.android.sun.activity.MainActivity;
import com.onyx.android.sun.activity.WifiActivity;
import com.onyx.android.sun.scribble.ScribbleActivity;

/**
 * Created by hehai on 17-10-13.
 */

public class ManagerActivityUtils {
    public static void startScribbleActivity(Context context, String questionID, String questionTitle, String question) {
        Intent intent = new Intent(context, ScribbleActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.QUESTION_ID, questionID);
        intent.putExtra(Constants.QUESTION_TITLE, questionTitle);
        intent.putExtra(Constants.QUESTION_TAG, question);
        context.startActivity(intent);
    }

    public static void startResetDeviceActivity(Context context) {
        Intent intent = new Intent("android.settings.PRIVACY_SETTINGS");
        intent.setClassName("com.android.settings", "com.android.settings.PrivacySettings");
        context.startActivity(intent);
    }

    public static void startWifiActivity(Context context) {
        Intent intent = new Intent(context, WifiActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void startMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    public static void startLoginActivity(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }
}
