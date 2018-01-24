package com.onyx.edu.homework.action;

import android.content.Context;
import android.widget.Toast;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.homework.Question;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.edu.homework.base.ActionChain;
import com.onyx.edu.homework.base.BaseAction;

import java.util.List;

/**
 * Created by lxm on 2017/12/5.
 */

public class HomeworkListActionChain extends BaseAction {

    private String publicHomeworkId;
    private String personalHomeworkId;
    private List<Question> questions;

    public HomeworkListActionChain(String publicHomeworkId, String personalHomeworkId) {
        this.publicHomeworkId = publicHomeworkId;
        this.personalHomeworkId = personalHomeworkId;
    }

    @Override
    public void execute(final Context context, final BaseCallback baseCallback) {
        final HomeworkListAction homeworkListAction = new HomeworkListAction(publicHomeworkId, personalHomeworkId);
        final GetHomeworkReviewsAction answersAction = new GetHomeworkReviewsAction(publicHomeworkId, personalHomeworkId, homeworkListAction.getQuestions(), false, false);
        final ActionChain chain = new ActionChain(false);
        chain.addAction(new CloudIndexServiceAction());
        chain.addAction(new GetTokenFromLocalAction());
        chain.addAction(homeworkListAction);
        chain.addAction(answersAction);
        chain.execute(context, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                questions = homeworkListAction.getQuestions();
                if (e != null && CollectionUtils.isNullOrEmpty(questions)) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                baseCallback.done(request, e);
            }
        });
    }

    public List<Question> getQuestions() {
        return questions;
    }
}
