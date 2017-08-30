package com.onyx.edu.note.actions.scribble;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.view.WindowManager;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.asyncrequest.navigation.ExportEditedPicRequest;
import com.onyx.android.sdk.scribble.asyncrequest.navigation.PageListRenderRequest;
import com.onyx.android.sdk.ui.dialog.DialogProgress;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.edu.note.NoteApplication;
import com.onyx.edu.note.actions.BaseNoteAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by solskjaer49 on 17/3/10 14:46.
 */
public class ExportEditedPicAction extends BaseNoteAction {
    private DialogProgress progress;
    private List<String> pageUniqueIds;
    private String douId;
    private Rect size;
    private String noteTitle;
    private int count;

    public ExportEditedPicAction(WindowManager wm, String douId, String currentPageUniqueId, Uri exportedPicUri) {
        pageUniqueIds = new ArrayList<>();
        pageUniqueIds.add(currentPageUniqueId);

        this.douId = douId;
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        this.size = new Rect(0, 0, width, height);
        this.noteTitle = FileUtils.getBaseName(FileUtils.getRealFilePathFromUri(NoteApplication.getInstance(), exportedPicUri)).replaceAll(":", " ");
    }

    @Override
    public void execute(NoteManager noteManager, BaseCallback callback) {
        count = pageUniqueIds.size();
//        progress = new DialogProgress(activity, 0, count);
//        progress.setCanceledOnTouchOutside(false);
//        progress.setTitle(activity.getString(R.string.exporting_info));
//        String location = null;
//        try {
//            location = activity.getString(R.string.export_location, ExportUtils.getExportPicPath(noteTitle));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        progress.setSubTitle(location);
//        progress.show();
//
//        progress.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialogInterface) {
//                callback.done(null, null);
//            }
//        });
        getPageBitmap(noteManager,callback);
    }

    private void getPageBitmap(final NoteManager noteManager, final BaseCallback callback) {
        if (pageUniqueIds == null || pageUniqueIds.size() == 0) {
            callback.done(null, null);
            return;
        }
        String pageUniqueId = pageUniqueIds.remove(0);
        final int index = 1;
        List<PageInfo> pageInfoList = new ArrayList<>();
        PageInfo pageInfo = new PageInfo(pageUniqueId, size.width(), size.height());
        pageInfo.updateDisplayRect(new RectF(0, 0, size.width(), size.height()));
        pageInfoList.add(pageInfo);
        final PageListRenderRequest renderRequest = new PageListRenderRequest(douId, pageInfoList, size, false, true);
        noteManager.submitRequest(renderRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
//                    onExportFail(activity, progress);
                    return;
                }
                exportPage(noteManager, renderRequest.getRenderBitmap(), index);
                getPageBitmap(noteManager,callback);
            }
        });
    }

    private void exportPage(final NoteManager noteManager, final Bitmap bitmap, final int index) {
        ExportEditedPicRequest exportNoteRequest = new ExportEditedPicRequest(bitmap, noteTitle);
        noteManager.submitRequest(exportNoteRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (!bitmap.isRecycled()) {
                    bitmap.recycle();
                }
                if (e != null) {
//                    onExportFail(activity, progress);
                    return;
                }

//                progress.setProgress(index);
                if (index == count) {
//                    onExportSuccess(activity, progress);
                }
            }
        });
    }

//    private void onExportFail(final T activity, DialogProgress progress) {
//        progress.setTitle(activity.getString(R.string.export_fail));
//        progress.getProgressBar().setVisibility(View.GONE);
//    }
//
//    private void onExportSuccess(final T activity, DialogProgress progress) {
//        progress.setTitle(activity.getString(R.string.export_success));
//        progress.dismiss();
//    }
}
