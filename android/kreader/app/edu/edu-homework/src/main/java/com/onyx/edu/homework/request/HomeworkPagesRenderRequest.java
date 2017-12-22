package com.onyx.edu.homework.request;


import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.edu.homework.data.Constant;
import com.onyx.edu.homework.utils.BitmapUtils;

import java.io.File;
import java.util.List;

/**
 * Created by lxm on 2017/12/5.
 * load and render shape with scale and offset for homework visible pages.
 */
public class HomeworkPagesRenderRequest extends BaseNoteRequest {

    private Bitmap renderBitmap;
    private boolean saveAsFile;
    private String filePath;
    private String drawText;

    public HomeworkPagesRenderRequest(final String id,
                                      final List<PageInfo> pages,
                                      final Rect size,
                                      final String text,
                                      final boolean saveAsFile) {
        setDocUniqueId(id);
        setAbortPendingTasks(false);
        setViewportSize(size);
        setVisiblePages(pages);
        setPauseInputProcessor(true);
        setResumeInputProcessor(false);
        this.saveAsFile = saveAsFile;
        this.drawText = text;
    }

    public void execute(final NoteViewHelper parent) throws Exception {
        parent.reset();
        parent.setDrawText(drawText);
        ensureDocumentOpened(parent);
        updateShapeDataInfo(parent);
        loadShapeData(parent);
        renderVisiblePages(parent);
        if (saveAsFile) {
            filePath = saveToFile(parent.getRenderBitmap(), getDocUniqueId(), getVisiblePages().get(0).getName());
        }else {
            renderBitmap = Bitmap.createBitmap(parent.getRenderBitmap());
        }
        parent.reset();
    }

    private String saveToFile(Bitmap bitmap, String documentId, String fileName) throws Exception {
        File file = new File(Constant.getRenderPagePath(documentId, fileName));
        if (!file.exists()){
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        FileUtils.saveBitmapToFile(bitmap, file, Bitmap.CompressFormat.JPEG, 100);
        return file.getAbsolutePath();
    }

    private void loadShapeData(final NoteViewHelper parent) {
        try {
            parent.getNoteDocument().loadShapePages(getContext(), getVisiblePages());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap getRenderBitmap() {
        return renderBitmap;
    }

    public String getFilePath() {
        return filePath;
    }
}
