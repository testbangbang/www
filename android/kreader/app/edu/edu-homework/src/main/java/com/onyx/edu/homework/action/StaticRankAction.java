package com.onyx.edu.homework.action;

import android.content.Context;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.edu.homework.base.BaseAction;
import com.onyx.edu.homework.request.StaticRankRequest;

/**
 * Created by lxm on 2018/1/31.
 */

public class StaticRankAction extends BaseAction {

    private String publicHomeworkId;

    public StaticRankAction(String publicHomeworkId) {
        this.publicHomeworkId = publicHomeworkId;
    }

    @Override
    public void execute(Context context, BaseCallback baseCallback) {
        StaticRankRequest rankRequest = new StaticRankRequest(publicHomeworkId);
        getCloudManager().submitRequest(context, rankRequest, baseCallback);
    }
}
