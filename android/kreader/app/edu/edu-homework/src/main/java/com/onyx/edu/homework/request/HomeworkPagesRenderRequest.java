package com.onyx.edu.homework.request;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.data.model.homework.Question;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.TextLayoutArgs;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.edu.homework.data.Constant;
import com.onyx.edu.homework.utils.ObjectiveQuestionBitmapUtil;

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
    private TextLayoutArgs textLayoutArgs;
    private Question question;

    public HomeworkPagesRenderRequest(final String id,
                                      final List<PageInfo> pages,
                                      final Rect size,
                                      final TextLayoutArgs args,
                                      final boolean saveAsFile) {
        setDocUniqueId(id);
        setAbortPendingTasks(false);
        setViewportSize(size);
        setVisiblePages(pages);
        setPauseInputProcessor(true);
        setResumeInputProcessor(false);
        this.saveAsFile = saveAsFile;
        this.textLayoutArgs = args;
    }

    public HomeworkPagesRenderRequest setQuestion(Question question) {
        this.question = question;
        return this;
    }

    public void execute(final NoteViewHelper parent) throws Exception {
        parent.reset();
        parent.setTextLayoutArgs(textLayoutArgs);
        ensureDocumentOpened(parent);
        updateShapeDataInfo(parent);
        loadShapeData(parent);
        renderVisiblePages(parent);
        if (saveAsFile) {
            benchmarkStart();
            filePath = saveToFile(parent.getRenderBitmap(), getDocUniqueId(), getVisiblePages().get(0).getName());
            Debug.d(getClass(), "saveToFile:" + benchmarkEnd());
        }else {
            renderBitmap = Bitmap.createBitmap(parent.getRenderBitmap());
        }
        parent.reset();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (saveAsFile && null != question && question.isChoiceQuestion()) {
            Bitmap objectiveQuestionBitmap =
                    ObjectiveQuestionBitmapUtil.createObjectiveQuestionBitmap(getContext(), question);
            canvas.drawBitmap(objectiveQuestionBitmap, 0, 0, null);
        }
    }

    private String saveToFile(Bitmap bitmap, String documentId, String fileName) throws Exception {
        File file = new File(Constant.getRenderPagePath(documentId, fileName));
        if (!file.exists()){
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        FileUtils.saveBitmapToFile(bitmap, file, Bitmap.CompressFormat.PNG, 70);
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
