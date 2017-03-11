package com.onyx.android.note.actions.scribble;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.view.View;

import com.onyx.android.note.R;
import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.note.activity.BaseScribbleActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.request.navigation.ExportEditedPicRequest;
import com.onyx.android.sdk.scribble.request.navigation.PageListRenderRequest;
import com.onyx.android.sdk.ui.dialog.DialogProgress;
import com.onyx.android.sdk.utils.ExportUtils;
import com.onyx.android.sdk.utils.FileUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by solskjaer49 on 17/3/10 14:46.
 */
public class ExportEditedPicAction<T extends BaseScribbleActivity> extends BaseNoteAction<T> {

    private DialogProgress progress;
    private List<String> pageUniqueIds;
    private String douId;
    private Rect size;
    private String noteTitle;
    private int count;

    public ExportEditedPicAction(final T activity, String douId, String currentPageUniqueId, Uri exportedPicUri) {
        pageUniqueIds = new ArrayList<>();
        pageUniqueIds.add(currentPageUniqueId);

        this.douId = douId;
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay().getHeight();
        this.size = new Rect(0, 0, width, height);
        this.noteTitle = FileUtils.getBaseName(FileUtils.getRealFilePathFromUri(activity, exportedPicUri)).replaceAll(":", " ");
    }

    @Override
    public void execute(final T activity, final BaseCallback callback) {
        count = pageUniqueIds.size();
        progress = new DialogProgress(activity, 0, count);
        progress.setCanceledOnTouchOutside(false);
        progress.setTitle(activity.getString(R.string.exporting_info));
        String location = null;
        try {
            location = activity.getString(R.string.export_location, ExportUtils.getExportPicPath(noteTitle));
        } catch (IOException e) {
            e.printStackTrace();
        }
        progress.setSubTitle(location);
        progress.show();

        progress.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                callback.done(null, null);
            }
        });
        getPageBitmap(activity);
    }

    private void getPageBitmap(final T activity) {
        if (pageUniqueIds == null || pageUniqueIds.size() == 0) {
            return;
        }
        String pageUniqueId = pageUniqueIds.remove(0);
        final int index = 1;
        List<PageInfo> pageInfoList = new ArrayList<>();
        PageInfo pageInfo = new PageInfo(pageUniqueId, size.width(), size.height());
        pageInfo.updateDisplayRect(new RectF(0, 0, size.width(), size.height()));
        pageInfoList.add(pageInfo);
        final PageListRenderRequest renderRequest = new PageListRenderRequest(douId, pageInfoList, size, false, true);
        activity.submitRequest(renderRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    onExportFail(activity, progress);
                    return;
                }
                exportPage(activity, renderRequest.getRenderBitmap(), index);
                getPageBitmap(activity);
            }
        });
    }

    private void exportPage(final T activity, final Bitmap bitmap, final int index) {
        ExportEditedPicRequest exportNoteRequest = new ExportEditedPicRequest(bitmap, noteTitle);
        activity.submitRequest(exportNoteRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (!bitmap.isRecycled()) {
                    bitmap.recycle();
                }
                if (e != null) {
                    onExportFail(activity, progress);
                    return;
                }

                progress.setProgress(index);
                if (index == count) {
                    onExportSuccess(activity, progress);
                }
            }
        });
    }

    private void onExportFail(final T activity, DialogProgress progress) {
        progress.setTitle(activity.getString(R.string.export_fail));
        progress.getProgressBar().setVisibility(View.GONE);
    }

    private void onExportSuccess(final T activity, DialogProgress progress) {
        progress.setTitle(activity.getString(R.string.export_success));
        progress.dismiss();
    }
}
