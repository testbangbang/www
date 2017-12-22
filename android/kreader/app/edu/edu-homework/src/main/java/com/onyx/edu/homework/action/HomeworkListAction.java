package com.onyx.edu.homework.action;

import android.content.Context;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.HomeworkRequestModel;
import com.onyx.android.sdk.data.model.Question;
import com.onyx.edu.homework.DataBundle;
import com.onyx.edu.homework.base.BaseAction;
import com.onyx.edu.homework.request.HomeworkListRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lxm on 2017/12/5.
 */

public class HomeworkListAction extends BaseAction {

    private String libraryId;
    private List<Question> questions = new ArrayList<>();

    public HomeworkListAction(String libraryId) {
        this.libraryId = libraryId;
    }

    @Override
    public void execute(Context context, final BaseCallback baseCallback) {
        final HomeworkListRequest listRequest = new HomeworkListRequest(libraryId);
        getCloudManager().submitRequest(context, listRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                HomeworkRequestModel homeworkRequestModel = listRequest.getHomeworkRequestModel();
                if (homeworkRequestModel != null) {
                    questions.addAll(homeworkRequestModel.questions);
                }
                DataBundle.getInstance().getHomeworkInfo().loadFromHomeworkRequestModel(homeworkRequestModel);
                baseCallback.done(request, e);
            }
        });
    }

    public List<Question> getQuestions() {
        return questions;
    }
}
