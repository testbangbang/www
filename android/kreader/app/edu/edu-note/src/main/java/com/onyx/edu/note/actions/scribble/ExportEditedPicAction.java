package com.onyx.edu.note.actions.scribble;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.view.View;
import android.view.WindowManager;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.asyncrequest.navigation.ExportEditedPicRequest;
import com.onyx.android.sdk.scribble.asyncrequest.navigation.PageListRenderRequest;
import com.onyx.android.sdk.ui.dialog.DialogProgress;
import com.onyx.android.sdk.utils.ExportUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.edu.note.R;
import com.onyx.edu.note.actions.BaseNoteAction;

import java.io.IOException;
import java.lang.ref.WeakReference;
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
    private WeakReference<Context> contextWeakReference;

    public ExportEditedPicAction(Context context, String douId, String currentPageUniqueId, Uri exportedPicUri) {
        pageUniqueIds = new ArrayList<>();
        pageUniqueIds.add(currentPageUniqueId);
        contextWeakReference = new WeakReference<>(context);
        this.douId = douId;
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int width = 0, height = 0;
        if (wm != null) {
            width = wm.getDefaultDisplay().getWidth();
            height = wm.getDefaultDisplay().getHeight();
        }
        this.size = new Rect(0, 0, width, height);
        this.noteTitle = FileUtils.getBaseName(FileUtils.getRealFilePathFromUri(contextWeakReference.get(), exportedPicUri)).replaceAll(":", " ");
    }

    @Override
    public void execute(NoteManager noteManager, final BaseCallback callback) {
        Context context = contextWeakReference.get();
        if (context == null) {
            return;
        }
        count = pageUniqueIds.size();
        progress = new DialogProgress(context, 0, count);
        progress.setCanceledOnTouchOutside(false);
        progress.setTitle(context.getString(R.string.exporting_info));
        String location = null;
        try {
            location = context.getString(R.string.export_location, ExportUtils.getExportPicPath(noteTitle));
        } catch (IOException e) {
            e.printStackTrace();
        }
        progress.setSubTitle(location);
        progress.show();
        getPageBitmap(noteManager);
        progress.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                callback.done(null, null);
            }
        });
    }

    private void getPageBitmap(final NoteManager noteManager) {
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
        noteManager.submitRequest(renderRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    return;
                }
                exportPage(noteManager, renderRequest.getRenderBitmap(), index);
                getPageBitmap(noteManager);
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
                    onExportFail(progress);
                }
                progress.setProgress(index);
                if (index == count) {
                    onExportSuccess(progress);
                }
            }
        });
    }

    private void onExportFail(DialogProgress progress) {
        progress.setTitle(contextWeakReference.get().getString(R.string.export_fail));
        progress.getProgressBar().setVisibility(View.GONE);
    }

    private void onExportSuccess(DialogProgress progress) {
        progress.setTitle(contextWeakReference.get().getString(R.string.export_success));
        progress.dismiss();
    }
}
