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

    private String libraryId;
    private List<Question> questions;

    public HomeworkListActionChain(String libraryId) {
        this.libraryId = libraryId;
    }

    @Override
    public void execute(Context context, final BaseCallback baseCallback) {
        showLoadingDialog(context, R.string.loading);
        final HomeworkListAction homeworkListAction = new HomeworkListAction(libraryId);
        final CheckLocalDataAction checkLocalDataAction = new CheckLocalDataAction(homeworkListAction.getQuestions(), libraryId);
        ActionChain chain = new ActionChain(true);
        chain.addAction(new GetTokenFromLocalAction());
        chain.addAction(new CloudIndexServiceAction());
        chain.addAction(homeworkListAction);
        chain.addAction(checkLocalDataAction);
        chain.execute(context, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                DataBundle.getInstance().setState(checkLocalDataAction.getCurrentState());
                questions = checkLocalDataAction.getQuestions();
                hideLoadingDialog();
                baseCallback.done(request, e);
            }
        });
    }

    public List<Question> getQuestions() {
        return questions;
    }
}
