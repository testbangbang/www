package com.onyx.android.dr.common;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.onyx.android.dr.R;
import com.onyx.android.dr.activity.AddInformalEssayActivity;
import com.onyx.android.dr.activity.AddMemorandumActivity;
import com.onyx.android.dr.activity.ApplicationsActivity;
import com.onyx.android.dr.activity.DictQueryActivity;
import com.onyx.android.dr.activity.DictResultShowActivity;
import com.onyx.android.dr.activity.EBookStoreActivity;
import com.onyx.android.dr.activity.GoodSentenceNotebookActivity;
import com.onyx.android.dr.activity.GoodSentenceTypeActivity;
import com.onyx.android.dr.activity.HearAndSpeakActivity;
import com.onyx.android.dr.activity.InformalEssayActivity;
import com.onyx.android.dr.activity.LoginActivity;
import com.onyx.android.dr.activity.MemorandumActivity;
import com.onyx.android.dr.activity.MyNotesActivity;
import com.onyx.android.dr.activity.NewWordNotebookActivity;
import com.onyx.android.dr.activity.NewWordQueryActivity;
import com.onyx.android.dr.activity.NewWordTypeActivity;
import com.onyx.android.dr.activity.PencilSketchActivity;
import com.onyx.android.dr.activity.QueryRecordActivity;
import com.onyx.android.dr.activity.RecordTimeSettingActivity;
import com.onyx.android.dr.activity.SpeechRecordingActivity;
import com.onyx.android.dr.reader.data.OpenBookParam;
import com.onyx.android.dr.reader.utils.ReaderUtil;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.utils.NetworkUtil;

/**
 * Created by hehai on 17-6-29.
 */

public class ActivityManager {
    public static void startLoginActivity(Context context) {
        if (enableWifiOpenAndDetect(context)) {
            CommonNotices.showMessage(context, context.getString(R.string.network_not_connected));
            return;
        }
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    public static void startDictQueryActivity(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, DictQueryActivity.class);
        context.startActivity(intent);
    }

    public static void startDictResultShowActivity(Context context, String editQuery, int dictType) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.EDITQUERY, editQuery);
        intent.putExtra(Constants.DICTTYPE, dictType);
        intent.setClass(context, DictResultShowActivity.class);
        context.startActivity(intent);
    }

    public static void startMyNotesActivity(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, MyNotesActivity.class);
        context.startActivity(intent);
    }

    public static void startGoodSentenceTypeActivity(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, GoodSentenceTypeActivity.class);
        context.startActivity(intent);
    }

    public static void startGoodSentenceNotebookActivity(Context context, int dictType) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.DICTTYPE, dictType);
        intent.setClass(context, GoodSentenceNotebookActivity.class);
        context.startActivity(intent);
    }

    public static void startQueryRecordActivity(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, QueryRecordActivity.class);
        context.startActivity(intent);
    }

    public static void startNewWordTypeActivity(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, NewWordTypeActivity.class);
        context.startActivity(intent);
    }

    public static void startNewWordNotebookActivity(Context context, int dictType) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.DICTTYPE, dictType);
        intent.setClass(context, NewWordNotebookActivity.class);
        context.startActivity(intent);
    }

    public static void startNewWordQueryActivity(Context context, String editQuery) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.EDITQUERY, editQuery);
        intent.setClass(context, NewWordQueryActivity.class);
        context.startActivity(intent);
    }

    public static void startInformalEssayActivity(Context context, int jumpSource) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.JUMP_SOURCE, jumpSource);
        intent.setClass(context, InformalEssayActivity.class);
        context.startActivity(intent);
    }

    public static void startAddInformalEssayActivity(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, AddInformalEssayActivity.class);
        context.startActivity(intent);
    }

    public static void startMemorandumActivity(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, MemorandumActivity.class);
        context.startActivity(intent);
    }

    public static void startAddMemorandumActivity(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, AddMemorandumActivity.class);
        context.startActivity(intent);
    }

    public static void startHearAndSpeakActivity(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, HearAndSpeakActivity.class);
        context.startActivity(intent);
    }

    public static void startPencilSketchActivity(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, PencilSketchActivity.class);
        context.startActivity(intent);
    }

    public static void startRecordTimeSettingActivity(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, RecordTimeSettingActivity.class);
        context.startActivity(intent);
    }

    public static void startSpeechRecordingActivity(Context context, String content) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.INFORMAL_ESSAY_CONTENT, content);
        intent.setClass(context, SpeechRecordingActivity.class);
        context.startActivity(intent);
    }

    public static void startScribbleActivity(Context context) {
        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(
                    Constants.SCRIBBLE_ACTIVITY_PACKAGE_NAME);
            if (intent != null) {
                ComponentName componentName = new ComponentName(Constants.SCRIBBLE_ACTIVITY_PACKAGE_NAME,
                        Constants.SCRIBBLE_ACTIVITY_FULL_PATH);
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setComponent(componentName);
                context.startActivity(intent);
            } else {
                CommonNotices.showMessage(context, context.getString(R.string.do_not_install_note_apk));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void startNoteApp(Context context) {
        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(
                    Constants.SCRIBBLE_ACTIVITY_PACKAGE_NAME);
            if (intent != null) {
                ComponentName componentName = new ComponentName(Constants.SCRIBBLE_ACTIVITY_PACKAGE_NAME,
                        Constants.STARTUP_ACTIVITY_FULL_PATH);
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setComponent(componentName);
                context.startActivity(intent);
            } else {
                CommonNotices.showMessage(context, context.getString(R.string.do_not_install_note_apk));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void openBook(Context context, Metadata metadata, String localPath) {
        OpenBookParam openBookParam = new OpenBookParam();
        openBookParam.setBookName(metadata.getName());
        openBookParam.setLocalPath(localPath);
        ReaderUtil.openBook(context, openBookParam);
    }

    public static void startApplicationsActivity(Context context) {
        Intent intent = new Intent(context, ApplicationsActivity.class);
        context.startActivity(intent);
    }

    public static void startEBookStoreActivity(Context context) {
        Intent intent = new Intent(context, EBookStoreActivity.class);
        context.startActivity(intent);
    }

    private static boolean enableWifiOpenAndDetect(Context context) {
        if (!NetworkUtil.isWiFiConnected(context)) {
            Device.currentDevice().enableWifiDetect(context);
            NetworkUtil.enableWiFi(context, true);
            return true;
        }
        return false;
    }
}
