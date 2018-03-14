package com.onyx.edu.homework.action;

import android.content.Context;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.model.homework.Homework;
import com.onyx.edu.homework.base.BaseAction;
import com.onyx.edu.homework.request.UpdateHomeworkModelRequest;

import java.util.List;

/**
 * Created by lxm on 2018/1/18.
 */

public class UpdateHomeworkModelAction extends BaseAction {

    private Homework homework;
    private String personalHomeworkId;

    public UpdateHomeworkModelAction(Homework homework, String personalHomeworkId) {
        this.homework = homework;
        this.personalHomeworkId = personalHomeworkId;
    }

    @Override
    public void execute(Context context, BaseCallback baseCallback) {
        UpdateHomeworkModelRequest modelRequest = new UpdateHomeworkModelRequest(homework, personalHomeworkId);
        getDataManager().submit(context, modelRequest, baseCallback);
    }
}
