package com.onyx.android.note.actions.scribble;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

import com.onyx.android.note.R;
import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.note.activity.BaseScribbleActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.request.navigation.ExportNoteRequest;
import com.onyx.android.sdk.scribble.request.navigation.PageListRenderRequest;
import com.onyx.android.sdk.ui.dialog.DialogProgress;
import com.onyx.android.sdk.utils.ExportUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ming on 12/6/16.
 */
public class ExportNoteAction<T extends BaseScribbleActivity> extends BaseNoteAction<T> {

    private DialogProgress progress;
    private List<String> pageUniqueIds;
    private String douId;
    private Rect size;
    private String noteTitle;
    private int count;
    private boolean exportCurPage;
    private int currentPageIndex;

    public ExportNoteAction(final T activity, String douId, List<String> pageNameList, String noteTitle, boolean exportCurPage, int currentPageIndex) {
        pageUniqueIds = new ArrayList<>();
        if (exportCurPage) {
            String currentPageUniqueId = pageNameList.get(currentPageIndex);
            pageUniqueIds.add(currentPageUniqueId);
        }else {
            pageUniqueIds = pageNameList;
        }

        this.exportCurPage = exportCurPage;
        this.currentPageIndex = currentPageIndex;
        this.douId = douId;
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay().getHeight();
        this.size = new Rect(0, 0, width, height);
        this.noteTitle = noteTitle.replaceAll(":", " ");
    }

    @Override
    public void execute(final T activity, final BaseCallback callback) {
        count = exportCurPage ? 1 : pageUniqueIds.size();
        progress = new DialogProgress(activity, 0, count);
        progress.setTitle(activity.getString(R.string.exporting_info));
        String location = activity.getString(R.string.export_location, ExportUtils.NOTE_EXPORT_LOCATION + noteTitle);
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
        final int index = exportCurPage ? currentPageIndex + 1 : count - pageUniqueIds.size();
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
        ExportNoteRequest exportNoteRequest = new ExportNoteRequest(bitmap, noteTitle, String.valueOf(index));
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

                if (exportCurPage) {
                    progress.setProgress(1);
                    onExportSuccess(activity, progress);
                } else {
                    progress.setProgress(index);
                    if (index == count) {
                        onExportSuccess(activity, progress);
                    }
                }
            }
        });
    }

    private void onExportFail(final T activity, DialogProgress progress) {
        progress.setTitle(activity.getString(R.string.export_fail));
        progress.getProgressBar().setVisibility(View.GONE);
    }

    private void onExportSuccess(final T activity, final DialogProgress progress) {
        progress.setTitle(activity.getString(R.string.export_success));
        progress.enableDismissButton(activity.getString(android.R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.dismiss();
            }
        });
    }
}
