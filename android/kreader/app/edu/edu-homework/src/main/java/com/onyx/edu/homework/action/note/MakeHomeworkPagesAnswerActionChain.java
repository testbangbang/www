package com.onyx.edu.homework.action.note;

import android.graphics.Rect;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.HomeworkSubmitAnswer;
import com.onyx.android.sdk.data.model.Question;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.edu.homework.base.BaseNoteAction;
import com.onyx.edu.homework.base.NoteActionChain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lxm on 2017/12/8.
 */

public class MakeHomeworkPagesAnswerActionChain extends BaseNoteAction {

    private List<HomeworkSubmitAnswer> answers;
    private List<Question> questions;

    public MakeHomeworkPagesAnswerActionChain(List<HomeworkSubmitAnswer> answers, List<Question> questions) {
        this.answers = answers;
        this.questions = questions;
    }

    @Override
    public void execute(NoteViewHelper noteViewHelper, final BaseCallback baseCallback) {
        if (answers == null || answers.isEmpty()) {
            BaseCallback.invoke(baseCallback, null, null);
            return;
        }
        List<String> docIds = new ArrayList<>();
        for (HomeworkSubmitAnswer answer : answers) {
            docIds.add(answer.uniqueId);
        }
        NoteActionChain chain = new NoteActionChain(true);
        GetPageUniqueIdsAction pageUniqueIdsAction = new GetPageUniqueIdsAction(docIds);
        final HomeworkPagesRenderAction listRenderAction = new HomeworkPagesRenderAction(pageUniqueIdsAction.getPageUniqueMap(),
                questions,
                true);
        chain.addAction(pageUniqueIdsAction);
        chain.addAction(listRenderAction);
        chain.execute(noteViewHelper, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                Map<String, List<String>> pageFilePathMap = listRenderAction.getPageFilePaths();
                for (HomeworkSubmitAnswer answer : answers) {
                    answer.setFilePaths(pageFilePathMap.get(answer.uniqueId));
                }
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }
}
