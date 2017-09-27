package com.onyx.android.dr.common;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import com.onyx.android.dr.R;
import com.onyx.android.dr.activity.AddInformalEssayActivity;
import com.onyx.android.dr.activity.AddMemorandumActivity;
import com.onyx.android.dr.activity.ApplicationsActivity;
import com.onyx.android.dr.activity.BaiduBaiKeActivity;
import com.onyx.android.dr.activity.BookDetailActivity;
import com.onyx.android.dr.activity.BookReportDetailActivity;
import com.onyx.android.dr.activity.BookReportListActivity;
import com.onyx.android.dr.activity.CreateGroupActivity;
import com.onyx.android.dr.activity.DictQueryActivity;
import com.onyx.android.dr.activity.DictResultShowActivity;
import com.onyx.android.dr.activity.DictSettingActivity;
import com.onyx.android.dr.activity.EBookStoreActivity;
import com.onyx.android.dr.activity.ExitGroupActivity;
import com.onyx.android.dr.activity.ForgetPasswordActivity;
import com.onyx.android.dr.activity.GoodSentenceNotebookActivity;
import com.onyx.android.dr.activity.GoodSentenceTypeActivity;
import com.onyx.android.dr.activity.GroupHomePageActivity;
import com.onyx.android.dr.activity.GroupMemberActivity;
import com.onyx.android.dr.activity.HearAndSpeakActivity;
import com.onyx.android.dr.activity.InformalEssayActivity;
import com.onyx.android.dr.activity.JoinGroupActivity;
import com.onyx.android.dr.activity.LoginActivity;
import com.onyx.android.dr.activity.MainActivity;
import com.onyx.android.dr.activity.ManageGroupActivity;
import com.onyx.android.dr.activity.MemorandumActivity;
import com.onyx.android.dr.activity.MyNotesActivity;
import com.onyx.android.dr.activity.NewWordNotebookActivity;
import com.onyx.android.dr.activity.NewWordQueryActivity;
import com.onyx.android.dr.activity.NewWordQueryDialogActivity;
import com.onyx.android.dr.activity.NewWordTypeActivity;
import com.onyx.android.dr.activity.PayActivity;
import com.onyx.android.dr.activity.PencilSketchActivity;
import com.onyx.android.dr.activity.QueryRecordActivity;
import com.onyx.android.dr.activity.RecordTimeSettingActivity;
import com.onyx.android.dr.activity.SearchBookActivity;
import com.onyx.android.dr.activity.SettingActivity;
import com.onyx.android.dr.activity.ShoppingCartActivity;
import com.onyx.android.dr.activity.SpeechRecordingActivity;
import com.onyx.android.dr.activity.SummaryListActivity;
import com.onyx.android.dr.activity.SystemUpdateHistoryActivity;
import com.onyx.android.dr.activity.UserInfoActivity;
import com.onyx.android.dr.activity.WifiActivity;
import com.onyx.android.dr.bean.NewWordBean;
import com.onyx.android.dr.event.MenuWifiSettingEvent;
import com.onyx.android.dr.reader.activity.AfterReadingActivity;
import com.onyx.android.dr.reader.activity.ReadSummaryActivity;
import com.onyx.android.dr.reader.common.ReaderConstants;
import com.onyx.android.dr.reader.data.OpenBookParam;
import com.onyx.android.dr.reader.utils.ReaderUtil;
import com.onyx.android.dr.reader.view.CustomDialog;
import com.onyx.android.dr.statistics.StatisticsActivity;
import com.onyx.android.dr.util.Utils;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.utils.NetworkUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;


/**
 * Created by hehai on 17-6-29.
 */

public class ActivityManager {
    public static void startLoginActivity(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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

    public static void startGoodSentenceNotebookActivity(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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

    public static void startNewWordNotebookActivity(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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

    public static void startNewWordQueryDialogActivity(Context context, NewWordBean bean) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.NEW_WORD_BEAN, bean);
        intent.setClass(context, NewWordQueryDialogActivity.class);
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

    public static void startReadingReportActivity(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, BookReportDetailActivity.class);
        context.startActivity(intent);
    }

    public static void startReadingReportActivity(Context context, Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, BookReportDetailActivity.class);
        context.startActivity(intent);
    }

    public static void startReadingReportListActivity(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, BookReportListActivity.class);
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

    public static void startSettingActivity(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, SettingActivity.class);
        context.startActivity(intent);
    }

    public static void startSpeechRecordingActivity(Context context, String title, String content) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.INFORMAL_ESSAY_TITLE, title);
        intent.putExtra(Constants.INFORMAL_ESSAY_CONTENT, content);
        intent.setClass(context, SpeechRecordingActivity.class);
        context.startActivity(intent);
    }

    public static void startGroupHomePageActivity(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, GroupHomePageActivity.class);
        context.startActivity(intent);
    }

    public static void startCreateGroupActivity(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, CreateGroupActivity.class);
        context.startActivity(intent);
    }

    public static void startJoinGroupActivity(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, JoinGroupActivity.class);
        context.startActivity(intent);
    }

    public static void startExitGroupActivity(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, ExitGroupActivity.class);
        context.startActivity(intent);
    }

    public static void startManageGroupActivity(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, ManageGroupActivity.class);
        context.startActivity(intent);
    }

    public static void startGroupMemberActivity(Context context, String id) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.GROUP_ID, id);
        intent.setClass(context, GroupMemberActivity.class);
        context.startActivity(intent);
    }

    public static void startDictSettingActivity(Context context, int jumpSource) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.JUMP_SOURCE, jumpSource);
        intent.setClass(context, DictSettingActivity.class);
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
        openBookParam.setBookId(metadata.getCloudId());
        ReaderUtil.openBook(context, openBookParam);
    }

    public static void startApplicationsActivity(Context context) {
        Intent intent = new Intent(context, ApplicationsActivity.class);
        context.startActivity(intent);
    }

    public static void startEBookStoreActivity(Context context) {
        if (enableWifiOpenAndDetect(context)) {
            CommonNotices.showMessage(context, context.getString(R.string.network_not_connected));
            return;
        }
        Intent intent = new Intent(context, EBookStoreActivity.class);
        context.startActivity(intent);
    }

    private static boolean enableWifiOpenAndDetect(Context context) {
        if (!NetworkUtil.isWiFiConnected(context)) {
            if (0 == Utils.getConfiguredNetworks(context)) {
                ActivityManager.startWifiActivity(context);
            } else {
                Device.currentDevice().enableWifiDetect(context);
                NetworkUtil.enableWiFi(context, true);
            }
            return true;
        }
        return false;
    }

    public static void startAfterReadingActivity(Context context, String bookName) {
        Intent intent = new Intent(context, AfterReadingActivity.class);
        intent.putExtra(ReaderConstants.AFTER_READING_ID, bookName);
        context.startActivity(intent);
    }

    public static void startSearchBookActivity(Context context) {
        Intent intent = new Intent(context, SearchBookActivity.class);
        context.startActivity(intent);
    }

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

    public static void startResetDeviceActivity(Context context) {
        Intent intent = new Intent("android.settings.PRIVACY_SETTINGS");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName("com.android.settings", "com.android.settings.PrivacySettings");
        context.startActivity(intent);
    }

    public static void startDateTimeSettingsActivity(Context context) {
        Intent intent = new Intent("com.android.settings.DateTimeSettingsSetupWizard");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName("com.android.settings", "com.android.settings.DateTimeSettingsSetupWizard");
        context.startActivity(intent);
    }

    public static void startSystemUpdateHistoryActivity(final Context context) {
        Intent intent = new Intent(context, SystemUpdateHistoryActivity.class);
        context.startActivity(intent);
    }

    public static void startInstallAPKActivity(Context context, File apkFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + apkFile.toString()),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public static void startReadSummaryActivity(Context context, String[] metadataArray) {
        Intent intent = new Intent(context, ReadSummaryActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.METADATA_ARRAY, metadataArray);
        context.startActivity(intent);
    }

    public static void startWifiActivity(Context context) {
        Intent intent = new Intent(context, WifiActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void startBookDetailActivity(Activity context, String bookId) {
        if (enableWifiOpenAndDetect(context)) {
            CommonNotices.showMessage(context, context.getString(R.string.network_not_connected));
            return;
        }
        Intent intent = new Intent(context, BookDetailActivity.class);
        intent.putExtra(Constant.ID_TAG, bookId);
        context.startActivityForResult(intent, 0);
    }

    public static void startPayActivity(final Activity context, String orderId) {
        if (enableWifiOpenAndDetect(context)) {
            CommonNotices.showMessage(context, context.getString(R.string.network_not_connected));
            return;
        }
        Intent intent = new Intent(context, PayActivity.class);
        intent.putExtra(Constants.ORDER_ID, orderId);
        context.startActivityForResult(intent, 0);
    }

    public static void startMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    public static void startShoppingCartActivity(Context context) {
        Intent intent = new Intent(context, ShoppingCartActivity.class);
        context.startActivity(intent);
    }

    public static void startForgetPasswordActivity(Context context) {
        Intent intent = new Intent(context, ForgetPasswordActivity.class);
        context.startActivity(intent);
    }

    public static void startUserInfoActivity(Context context) {
        Intent intent = new Intent(context, UserInfoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void startSummaryListActivity(Context context) {
        Intent intent = new Intent(context, SummaryListActivity.class);
        context.startActivity(intent);
    }

    public static void startStatisticsActivity(Context context) {
        Intent intent = new Intent(context, StatisticsActivity.class);
        context.startActivity(intent);
    }

    public static void startBaiduBaiKeActivity(Context context, String keyWord) {
        Intent intent = new Intent(context, BaiduBaiKeActivity.class);
        intent.putExtra(BaiduBaiKeActivity.BAIDU_BAIKE_PARAM_KEY, keyWord);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void startTTSSettingsActivity(Context context) {
        Intent intent = new Intent("com.android.settings.TextToSpeechSettings");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName("com.android.settings", "com.android.settings.TextToSpeechSettings");
        context.startActivity(intent);
    }
}
