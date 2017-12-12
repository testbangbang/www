package com.onyx.edu.homework.action;

import android.content.Context;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.model.HomeworkSubmitAnswer;
import com.onyx.android.sdk.data.model.HomeworkSubmitBody;
import com.onyx.edu.homework.base.BaseAction;
import com.onyx.edu.homework.request.HomeworkSubmitRequest;

import java.util.List;

/**
 * Created by lxm on 2017/12/9.
 */

public class HomeworkSubmitAction extends BaseAction {

    private String libraryId;

    private HomeworkSubmitBody body;

    public HomeworkSubmitAction(String libraryId, List<HomeworkSubmitAnswer> anwsers) {
        this.libraryId = libraryId;
        this.body = new HomeworkSubmitBody(anwsers);
    }

    @Override
    public void execute(Context context, BaseCallback baseCallback) {
        HomeworkSubmitRequest submitRequest = new HomeworkSubmitRequest(libraryId, body);
        getCloudManager().submitRequest(context, submitRequest,baseCallback);
    }

}
