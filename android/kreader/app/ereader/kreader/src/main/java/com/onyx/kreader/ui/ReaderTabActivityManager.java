package com.onyx.kreader.ui;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.onyx.android.sdk.utils.Debug;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by joy on 10/12/17.
 */

public class ReaderTabActivityManager {
    private static final Class TAG = ReaderTabActivityManager.class;

    private interface ReaderTabVisitor {
        void apply(ReaderTabManager.ReaderTab tab, int taskId);
    }

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
        intent.putExtra(ReaderBroadcastReceiver.TAG_DOCUMENT_REQUEST_FROM, ReaderBroadcastReceiver.TAG_DOCUMENT_REQUEST_TAB_HOST);
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

    public static void showTabHostMenuDialog(Context context, ReaderTabManager tabManager, ReaderTabManager.ReaderTab tab, View buttonMenu) {
        int[] location = new int[2];
        buttonMenu.getLocationOnScreen(location);
        int x = location[0] + 10;
        int y = buttonMenu.getHeight();
        ReaderBroadcastReceiver.sendShowTabHostMenuDialogIntent(context, tabManager.getTabReceiver(tab), x, y);
    }

    public static void notifyGotoPageLink(Context context, ReaderTabManager tabManager, ReaderTabManager.ReaderTab tab, String link) {
        ReaderBroadcastReceiver.sendGotoPageLinkIntent(context, tabManager.getTabReceiver(tab), link);
    }

    public static void notifyStopNoteWriting(Context context, ReaderTabManager tabManager, ReaderTabManager.ReaderTab tab) {
        ReaderBroadcastReceiver.sendStopNoteIntent(context, tabManager.getTabReceiver(tab));
    }

    public static boolean bringTabToFront(final Context context,
                                          final ReaderTabManager tabManager,
                                          final ReaderTabManager.ReaderTab tab,
                                          final boolean tabWidgetVisible) {
        final AtomicBoolean result = new AtomicBoolean(false);
        final ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        applyOnReaderTabs(context, tabManager,
                Arrays.asList(new ReaderTabManager.ReaderTab[]{tab}),
                new ReaderTabVisitor() {
                    @Override
                    public void apply(ReaderTabManager.ReaderTab tab, int taskId) {
                        Debug.d(TAG, "bring tab to front succeeded: " + tab);
                        am.moveTaskToFront(taskId, 0);
                        result.set(true);
                    }
                });
        return result.get();
    }

    public static void moveReaderTabToBack(final Context context, final ReaderTabManager tabManager, final ReaderTabManager.ReaderTab tab) {
        applyOnReaderTabs(context, tabManager,
                Arrays.asList(new ReaderTabManager.ReaderTab[]{tab}),
                new ReaderTabVisitor() {
                    @Override
                    public void apply(ReaderTabManager.ReaderTab tab, int taskId) {
                        Debug.d(TAG, "move tab to back succeeded: " + tab);
                        ReaderBroadcastReceiver.sendMoveTaskToBackIntent(context, tabManager.getTabReceiver(tab));
                    }
                });
    }

    public static void notifyTabActivated(final Context context, final ReaderTabManager tabManager, final ReaderTabManager.ReaderTab tab) {
        final String path = tabManager.getOpenedTabs().get(tab);
        applyOnOpenedReaderTabs(context, tabManager, new ReaderTabVisitor() {
            @Override
            public void apply(ReaderTabManager.ReaderTab tab, int taskId) {
                ReaderBroadcastReceiver.sendDocumentActivatedIntent(context, tabManager.getTabReceiver(tab), path);
            }
        });
    }

    public static void updateTabWidgetVisibilityOnOpenedReaderTabs(final Context context, final ReaderTabManager tabManager, final boolean visible) {
        applyOnOpenedReaderTabs(context, tabManager, new ReaderTabVisitor() {
            @Override
            public void apply(ReaderTabManager.ReaderTab tab, int taskId) {
                Debug.d(TAG, "update tab widget visibility: " + tab + ", " + visible);
                ReaderBroadcastReceiver.sendUpdateTabWidgetVisibilityIntent(context, tabManager.getTabReceiver(tab), visible);
            }
        });
    }

    public static void enableDebugLog(final Context context, final ReaderTabManager tabManager, final boolean enabled) {
        applyOnOpenedReaderTabs(context, tabManager, new ReaderTabVisitor() {
            @Override
            public void apply(ReaderTabManager.ReaderTab tab, int taskId) {
                if (enabled) {
                    ReaderBroadcastReceiver.sendEnableDebugLogIntent(context, tabManager.getTabReceiver(tab));
                } else {
                    ReaderBroadcastReceiver.sendDisableDebugLogIntent(context, tabManager.getTabReceiver(tab));
                }
            }
        });
    }

    private static void applyOnOpenedReaderTabs(Context context, ReaderTabManager tabManager, ReaderTabVisitor tabVisitor) {
        applyOnReaderTabs(context, tabManager, tabManager.getOpenedTabs().keySet(), tabVisitor);
    }

    private static void applyOnReaderTabs(Context context, ReaderTabManager tabManager,
                                          Collection<ReaderTabManager.ReaderTab> tabs,
                                          ReaderTabVisitor tabVisitor) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasksList = am.getRunningTasks(Integer.MAX_VALUE);
        if (tasksList.isEmpty()) {
            return;
        }
        for (ReaderTabManager.ReaderTab tab : tabs) {
            int nSize = tasksList.size();
            for (int i = 0; i < nSize; i++) {
                String clzName = tabManager.getTabActivity(tab).getName();
                if (tasksList.get(i).topActivity.getClassName().equals(clzName)) {
                    tabVisitor.apply(tab, tasksList.get(i).id);
                    break;
                }
            }
        }
    }

}
