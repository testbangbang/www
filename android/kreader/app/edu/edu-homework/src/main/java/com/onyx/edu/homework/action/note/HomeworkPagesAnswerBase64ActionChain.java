package com.onyx.edu.homework.action.note;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.HomeworkSubmitAnswer;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.edu.homework.DataBundle;
import com.onyx.edu.homework.base.BaseNoteAction;
import com.onyx.edu.homework.base.NoteActionChain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lxm on 2017/12/8.
 */

public class HomeworkPagesAnswerBase64ActionChain extends BaseNoteAction {

    private List<HomeworkSubmitAnswer> answers;
    private Rect size;

    public HomeworkPagesAnswerBase64ActionChain(List<HomeworkSubmitAnswer> answers, Rect size) {
        this.answers = answers;
        this.size = size;
    }

    @Override
    public void execute(NoteViewHelper noteViewHelper, final BaseCallback baseCallback) {
        noteViewHelper.reset();
        if (answers == null || answers.isEmpty()) {
            return;
        }
        List<String> docIds = new ArrayList<>();
        for (HomeworkSubmitAnswer answer : answers) {
            docIds.add(answer.question);
        }
        NoteActionChain chain = new NoteActionChain(true);
        GetPageUniqueIdsAction pageUniqueIdsAction = new GetPageUniqueIdsAction(docIds);
        final HomeworkPagesRenderAction listRenderAction = new HomeworkPagesRenderAction(pageUniqueIdsAction.getPageUniqueMap(),
                size,
                true);
        chain.addAction(pageUniqueIdsAction);
        chain.addAction(listRenderAction);
        chain.execute(noteViewHelper, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                Map<String, List<String>> pageBase64s = listRenderAction.getPageBase64s();
                for (HomeworkSubmitAnswer answer : answers) {
                    answer.setAttachment(pageBase64s.get(answer.question));
                }
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }

    public List<HomeworkSubmitAnswer> getAnswers() {
        return answers;
    }
}
