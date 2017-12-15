package com.onyx.edu.homework.action;

import android.content.Context;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.HomeworkSubmitAnswer;
import com.onyx.android.sdk.data.model.Question;
import com.onyx.edu.homework.DataBundle;
import com.onyx.edu.homework.R;
import com.onyx.edu.homework.base.BaseAction;
import com.onyx.edu.homework.data.HomeworkState;
import com.onyx.edu.homework.request.GetHomeworkReviewsRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lxm on 2017/12/13.
 */

public class GetHomeworkReviewsAction extends BaseAction {

    private String homeworkId;
    private List<Question> questions;
    private boolean showLoading = false;
    private HomeworkState currentState;

    public GetHomeworkReviewsAction(String homeworkId, List<Question> questions, boolean show) {
        this.homeworkId = homeworkId;
        this.questions = questions;
        showLoading = show;
    }

    @Override
    public void execute(final Context context, final BaseCallback baseCallback) {
        if (showLoading) {
            showLoadingDialog(context, context.getString(R.string.fetching_review));
        }
        final GetHomeworkReviewsRequest answersRequest = new GetHomeworkReviewsRequest(questions, homeworkId);
        getCloudManager().submitRequest(context, answersRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                currentState = answersRequest.getCurrentState();
                DataBundle.getInstance().setState(currentState);
                hideLoadingDialog();
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }
}
