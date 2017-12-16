package com.onyx.edu.homework.action;

import android.content.Context;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.Question;
import com.onyx.edu.homework.DataBundle;
import com.onyx.edu.homework.R;
import com.onyx.edu.homework.base.ActionChain;
import com.onyx.edu.homework.base.BaseAction;

import java.util.List;

/**
 * Created by lxm on 2017/12/5.
 */

public class HomeworkListActionChain extends BaseAction {

    private String homeworkId;
    private List<Question> questions;

    public HomeworkListActionChain(String homeworkId) {
        this.homeworkId = homeworkId;
    }

    @Override
    public void execute(Context context, final BaseCallback baseCallback) {
        final HomeworkListAction homeworkListAction = new HomeworkListAction(homeworkId);
        final GetHomeworkReviewsAction answersAction = new GetHomeworkReviewsAction(homeworkId, homeworkListAction.getQuestions(), false);
        final CheckLocalDataAction checkLocalDataAction = new CheckLocalDataAction(homeworkListAction.getQuestions(), homeworkId);
        final ActionChain chain = new ActionChain(true);
        chain.addAction(new CloudIndexServiceAction());
        chain.addAction(new GetTokenFromLocalAction());
        chain.addAction(homeworkListAction);
        chain.addAction(answersAction);
        chain.addAction(checkLocalDataAction);
        chain.execute(context, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                DataBundle.getInstance().setState(checkLocalDataAction.getCurrentState());
                questions = checkLocalDataAction.getQuestions();
                baseCallback.done(request, e);
            }
        });
    }

    public List<Question> getQuestions() {
        return questions;
    }
}
