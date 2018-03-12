package com.onyx.edu.homework.action;

import android.content.Context;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.homework.HomeworkSubmitAnswer;
import com.onyx.android.sdk.data.model.homework.HomeworkSubmitBody;
import com.onyx.edu.homework.DataBundle;
import com.onyx.edu.homework.base.BaseAction;
import com.onyx.edu.homework.data.HomeworkState;
import com.onyx.edu.homework.request.HomeworkSubmitRequest;

import java.util.List;

/**
 * Created by lxm on 2017/12/9.
 */

public class HomeworkSubmitAction extends BaseAction {

    private String publicHomeworkId;
    private String personalHomeworkId;

    private HomeworkSubmitBody body;

    public HomeworkSubmitAction(String publicHomeworkId, String personalHomeworkId, List<HomeworkSubmitAnswer> anwsers) {
        this.publicHomeworkId = publicHomeworkId;
        this.personalHomeworkId = personalHomeworkId;
        this.body = new HomeworkSubmitBody(anwsers);
    }

    @Override
    public void execute(Context context, final BaseCallback baseCallback) {
        final HomeworkSubmitRequest submitRequest = new HomeworkSubmitRequest(publicHomeworkId, personalHomeworkId, body);
        getCloudManager().submitRequest(context, submitRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (submitRequest.isSuccess()) {
                    if (DataBundle.getInstance().getState() == HomeworkState.REVIEW) {
                        DataBundle.getInstance().setState(HomeworkState.SUBMITTED_AFTER_REVIEW);
                    } else {
                        DataBundle.getInstance().setState(HomeworkState.SUBMITTED);
                    }
                    DataBundle.getInstance().getHomework().setHasReview(false);
                }
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }

}
