package com.onyx.edu.homework.request;

import android.content.Context;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.Question;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

import java.util.List;

/**
 * Created by lxm on 2017/12/8.
 */

public class CheckAnswerRequest extends BaseDataRequest {

    private volatile List<Question> questions;
    private Context context;

    public CheckAnswerRequest(List<Question> questions, Context context) {
        this.questions = questions;
        this.context = context;
    }


    @Override
    public void execute(DataManager dataManager) throws Exception {
        super.execute(dataManager);
        if (questions == null) {
            return;
        }
        for (Question question : questions) {
            if (question.isChoiceQuestion()) {
                continue;
            }
            question.setDoneAnswer(NoteDataProvider.checkHasShape(context.getApplicationContext(), question._id));
        }
    }


    public List<Question> getQuestions() {
        return questions;
    }
}
