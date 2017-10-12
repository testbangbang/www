package com.onyx.kreader.ui.actions;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.note.actions.FlushNoteAction;
import com.onyx.kreader.note.actions.StopNoteActionChain;
import com.onyx.kreader.note.request.StartNoteRequest;
import com.onyx.kreader.ui.ReaderActivity;
import com.onyx.kreader.ui.ReaderTabHostBroadcastReceiver;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.dialog.DialogImageView;
import com.onyx.kreader.ui.dialog.DialogTabHostMenu;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by joy on 2/13/17.
 */

public class ShowTabHostMenuDialogAction extends BaseAction {
    private SurfaceView surfaceView;
    private List<Metadata> list;
    private boolean isSideReadingMode;

    public ShowTabHostMenuDialogAction(SurfaceView surfaceView, List<Metadata> list, boolean isSideReadingMode) {
        this.surfaceView = surfaceView;
        this.list = list;
        this.isSideReadingMode = isSideReadingMode;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, BaseCallback baseCallback) {
        showTabHostMenuDialog(readerDataHolder, getRecentFiles(readerDataHolder, list));
    }

    private boolean isValidFilePath(String path) {
        if (StringUtils.isNullOrEmpty(path)) {
            return false;
        }
        File file = new File(path);
        return file.exists() && file.isFile();
    }

    private ArrayList<String> getRecentFiles(ReaderDataHolder dataHolder, List<Metadata> list) {
        ArrayList<String> files = new ArrayList<>();
        if (CollectionUtils.isNullOrEmpty(list)) {
            files.add(dataHolder.getDocumentPath());
            return files;
        }
        for (Metadata data : list) {
            if (isValidFilePath(data.getNativeAbsolutePath())) {
                files.add(data.getNativeAbsolutePath());
            }
        }
        return files;
    }

    private void showTabHostMenuDialog(final ReaderDataHolder dataHolder, List<String> files) {
        final DialogTabHostMenu dlg = new DialogTabHostMenu(dataHolder.getContext(), files, isSideReadingMode,
                new DialogTabHostMenu.Callback() {

                    @Override
                    public void onLinkedOpen(String path) {
                        ReaderTabHostBroadcastReceiver.sendSideReadingCallbackIntent(dataHolder.getContext(),
                                ReaderTabHostBroadcastReceiver.SideReadingCallback.DOUBLE_OPEN,
                                path, null);
                    }

                    @Override
                    public void onSideOpen(String left, String right) {
                        ReaderTabHostBroadcastReceiver.sendSideReadingCallbackIntent(dataHolder.getContext(),
                                ReaderTabHostBroadcastReceiver.SideReadingCallback.SIDE_OPEN,
                                left, right);
                    }

                    @Override
                    public void onSideNote(String path) {
                    }

                    @Override
                    public void onOpenDoc(String path) {
                        ReaderTabHostBroadcastReceiver.sendSideReadingCallbackIntent(dataHolder.getContext(),
                                ReaderTabHostBroadcastReceiver.SideReadingCallback.OPEN_NEW_DOC,
                                path, null);
                    }

                    @Override
                    public void onSideSwitch() {
                        ReaderTabHostBroadcastReceiver.sendSideReadingCallbackIntent(dataHolder.getContext(),
                                ReaderTabHostBroadcastReceiver.SideReadingCallback.SWITCH_SIDE,
                                null, null);
                    }

                    @Override
                    public void onClosing() {
                        ReaderTabHostBroadcastReceiver.sendSideReadingCallbackIntent(dataHolder.getContext(),
                                ReaderTabHostBroadcastReceiver.SideReadingCallback.QUIT_SIDE_READING,
                                null, null);
                    }
                });

        int[] location = new int[2];
        surfaceView.getLocationOnScreen(location);

        dlg.getWindow().setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams lp = dlg.getWindow().getAttributes();
        lp.x = location[0] + 10;
        lp.y = location[1];

        final boolean isNoteWriting = dataHolder.isNoteWritingProvider();

        dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dataHolder.removeActiveDialog(dlg);
                if (isNoteWriting) {
                    FlushNoteAction flushNoteAction = FlushNoteAction.resumeAfterFlush(dataHolder.getVisiblePages());
                    flushNoteAction.execute(dataHolder, null);
                }
            }
        });

        dataHolder.addActiveDialog(dlg);
        if (isNoteWriting) {
            FlushNoteAction flushNoteAction = FlushNoteAction.pauseAfterFlush(dataHolder.getVisiblePages());
            flushNoteAction.execute(dataHolder, null);
        }

        dlg.show();
    }
}
