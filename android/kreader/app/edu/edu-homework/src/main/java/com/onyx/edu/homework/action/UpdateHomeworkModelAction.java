package com.onyx.edu.homework.action;

import android.content.Context;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.model.homework.Homework;
import com.onyx.edu.homework.base.BaseAction;
import com.onyx.edu.homework.request.UpdateHomeworkModelRequest;

/**
 * Created by lxm on 2018/1/18.
 */

public class UpdateHomeworkModelAction extends BaseAction {

    private Homework homework;

    public UpdateHomeworkModelAction(Homework homework) {
        this.homework = homework;
    }

    @Override
    public void execute(Context context, BaseCallback baseCallback) {
        UpdateHomeworkModelRequest modelRequest = new UpdateHomeworkModelRequest(homework);
        getDataManager().submit(context, modelRequest, baseCallback);
    }
}
