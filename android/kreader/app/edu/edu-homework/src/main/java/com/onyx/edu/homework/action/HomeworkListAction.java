package com.onyx.edu.homework.action;

import android.content.Context;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.HomeworkRequestModel;
import com.onyx.edu.homework.base.BaseAction;
import com.onyx.edu.homework.request.HomeworkListRequest;

/**
 * Created by lxm on 2017/12/5.
 */

public class HomeworkListAction extends BaseAction {

    private String libraryId;
    private HomeworkRequestModel homeworkRequestModel;

    public HomeworkListAction(String libraryId) {
        this.libraryId = libraryId;
    }

    @Override
    public void execute(Context context, final BaseCallback baseCallback) {
        final HomeworkListRequest listRequest = new HomeworkListRequest(libraryId);
        getCloudManager().submitRequest(context, listRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                homeworkRequestModel = listRequest.getHomeworkRequestModel();
                baseCallback.done(request, e);
            }
        });
    }

    public HomeworkRequestModel getHomeworkRequestModel() {
        return homeworkRequestModel;
    }
}
