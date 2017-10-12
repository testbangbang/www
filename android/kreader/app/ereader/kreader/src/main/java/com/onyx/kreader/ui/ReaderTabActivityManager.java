package com.onyx.kreader.ui;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.onyx.android.sdk.utils.Debug;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by joy on 10/12/17.
 */

public class ReaderTabActivityManager {
    private static final Class TAG = ReaderTabActivityManager.class;

    public static void openDocument(Context context,
                                    ReaderTabManager tabManager,
                                    ReaderTabManager.ReaderTab tab,
                                    Intent srcIntent,
                                    String path,
                                    int windowGravity,
                                    int windowWidth,
                                    int windowHeight,
                                    boolean tabWidgetVisible,
                                    boolean isSideReading) {
        Debug.d(TAG, "openDocument: " + tab + ", " + path);
        Intent intent = new Intent(context, tabManager.getTabActivity(tab));
        intent.setAction(Intent.ACTION_VIEW);
        if (srcIntent == null) {
            intent.setData(Uri.fromFile(new File(path)));
        } else {
            intent.setDataAndType(Uri.fromFile(new File(path)), srcIntent.getType());
            intent.putExtras(srcIntent);
        }

        intent.putExtra(ReaderBroadcastReceiver.TAG_WINDOW_GRAVITY, windowGravity);
        intent.putExtra(ReaderBroadcastReceiver.TAG_WINDOW_WIDTH, windowWidth);
        intent.putExtra(ReaderBroadcastReceiver.TAG_WINDOW_HEIGHT, windowHeight);

        if (tabManager.getOpenedTabs().size() > 1) {
            intent.putExtra(ReaderBroadcastReceiver.TAG_TAB_WIDGET_VISIBLE, tabWidgetVisible);
        }
        if (isSideReading) {
            intent.putExtra(ReaderBroadcastReceiver.TAG_SIDE_READING_MODE, true);
        }

        ReaderBroadcastReceiver.sendStartReaderIntent(context, intent,
                tabManager.getTabReceiver(tab),
                tabManager.getTabActivity(tab)
        );
    }

    public static void closeTabActivity(Context context, ReaderTabManager tabManager, ReaderTabManager.ReaderTab tab) {
        ReaderBroadcastReceiver.sendCloseReaderIntent(context, tabManager.getTabReceiver(tab));
    }

    public static void updateTabWindow(Context context, ReaderTabManager tabManager, ReaderTabManager.ReaderTab tab,
                                       int gravity, int width, int height) {
        ReaderBroadcastReceiver.sendResizeReaderWindowIntent(context,
                tabManager.getTabReceiver(tab), gravity, width, height);
    }

    public static boolean bringTabToFront(Context context,
                                          ReaderTabManager tabManager,
                                          ReaderTabManager.ReaderTab tab,
                                          boolean tabWidgetVisible) {
        if (!tabManager.getOpenedTabs().containsKey(tab)) {
            return false;
        }

        String clzName = tabManager.getTabActivity(tab).getName();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasksList = am.getRunningTasks(Integer.MAX_VALUE);
        if (!tasksList.isEmpty()) {
            int nSize = tasksList.size();
            for (int i = 0; i < nSize; i++) {
                if (tasksList.get(i).topActivity.getClassName().equals(clzName)) {
                    Debug.d(TAG, "bring tab to front succeeded: " + tab);
                    // TODO
//                    updateCurrentTabInHost(tab);
//                    updateReaderTabWindowHeight(tab);
                    am.moveTaskToFront(tasksList.get(i).id, 0);

                    if (!tabManager.supportMultipleTabs() || tabManager.getOpenedTabs().size() <= 1) {
                        ReaderBroadcastReceiver.sendUpdateTabWidgetVisibilityIntent(context,
                                tabManager.getTabReceiver(tab), true);
                    } else {
                        ReaderBroadcastReceiver.sendUpdateTabWidgetVisibilityIntent(context,
                                tabManager.getTabReceiver(tab), tabWidgetVisible);
                    }
                    return true;
                }
            }
        }
        Debug.d(TAG, "bring tab to front failed: " + tab);
        return false;
    }

    public static boolean moveReaderTabToBack(Context context, ReaderTabManager tabManager, ReaderTabManager.ReaderTab tab) {
        String clzName = tabManager.getTabActivity(tab).getName();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasksList = am.getRunningTasks(Integer.MAX_VALUE);
        if (!tasksList.isEmpty()) {
            int nSize = tasksList.size();
            for (int i = 0; i < nSize; i++) {
                if (tasksList.get(i).topActivity.getClassName().equals(clzName)) {
                    Debug.d(TAG, "move tab to back succeeded: " + tab);
                    ReaderBroadcastReceiver.sendMoveTaskToBackIntent(context, tabManager.getTabReceiver(tab));
                    return true;
                }
            }
        }
        Debug.d(TAG, "move tab to back failed: " + tab);
        return false;
    }

    public static void notifyTabActivated(Context context, ReaderTabManager tabManager, ReaderTabManager.ReaderTab tab) {
        final String path = tabManager.getOpenedTabs().get(tab);
        for (LinkedHashMap.Entry<ReaderTabManager.ReaderTab, String> entry : tabManager.getOpenedTabs().entrySet()) {
            ReaderBroadcastReceiver.sendDocumentActivatedIntent(context, tabManager.getTabReceiver(entry.getKey()), path);
        }
    }

    public static void showTabHostMenuDialog(Context context, ReaderTabManager tabManager, ReaderTabManager.ReaderTab tab) {
        ReaderBroadcastReceiver.sendShowTabHostMenuDialogIntent(context, tabManager.getTabReceiver(tab));
    }

    public static void notifyGotoPageLink(Context context, ReaderTabManager tabManager, ReaderTabManager.ReaderTab tab, String link) {
        ReaderBroadcastReceiver.sendGotoPageLinkIntent(context, tabManager.getTabReceiver(tab), link);
    }

    public static void notifyStopNoteWriting(Context context, ReaderTabManager tabManager, ReaderTabManager.ReaderTab tab) {
        ReaderBroadcastReceiver.sendStopNoteIntent(context, tabManager.getTabReceiver(tab));
    }

    public static void updateTabWidgetVisibilityOnOpenedReaderTabs(Context context, ReaderTabManager tabManager, boolean visible) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasksList = am.getRunningTasks(Integer.MAX_VALUE);
        if (!tasksList.isEmpty()) {
            int nSize = tasksList.size();
            for (int i = 0; i < nSize; i++) {
                for (ReaderTabManager.ReaderTab tab : tabManager.getOpenedTabs().keySet()) {
                    String clzName = tabManager.getTabActivity(tab).getName();
                    if (tasksList.get(i).topActivity.getClassName().equals(clzName)) {
                        Debug.d(TAG, "update tab widget visibility: " + tab + ", " + visible);
                        ReaderBroadcastReceiver.sendUpdateTabWidgetVisibilityIntent(context, tabManager.getTabReceiver(tab), visible);
                        break;
                    }
                }
            }
        }
    }

    public static void enableDebugLog(Context context, ReaderTabManager tabManager, boolean enabled) {
        Debug.d(TAG, "enableDebugLog: " + enabled);
        enableDebugLogOnOpenedReaderTabs(context, tabManager, enabled);
    }

    private static void enableDebugLogOnOpenedReaderTabs(Context context,
                                                  ReaderTabManager tabManager,
                                                  boolean enabled) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasksList = am.getRunningTasks(Integer.MAX_VALUE);
        if (!tasksList.isEmpty()) {
            int nSize = tasksList.size();
            for (int i = 0; i < nSize; i++) {
                for (ReaderTabManager.ReaderTab tab : tabManager.getOpenedTabs().keySet()) {
                    String clzName = tabManager.getTabActivity(tab).getName();
                    if (tasksList.get(i).topActivity.getClassName().equals(clzName)) {
                        Debug.d(TAG, "set debug log: " + tab + ", " + enabled);
                        if (enabled) {
                            ReaderBroadcastReceiver.sendEnableDebugLogIntent(context, tabManager.getTabReceiver(tab));
                        } else {
                            ReaderBroadcastReceiver.sendDisableDebugLogIntent(context, tabManager.getTabReceiver(tab));
                        }
                        break;
                    }
                }
            }
        }
    }

}
