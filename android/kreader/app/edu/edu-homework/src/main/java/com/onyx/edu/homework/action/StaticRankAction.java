package com.onyx.edu.homework.action;

import android.content.Context;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.edu.homework.base.BaseAction;
import com.onyx.edu.homework.request.StaticRankRequest;

/**
 * Created by lxm on 2018/1/31.
 */

public class StaticRankAction extends BaseAction {

    private String childId;
    private String id;

    public StaticRankAction setChildId(String childId) {
        this.childId = childId;
        return this;
    }

    public StaticRankAction setId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public void execute(Context context, BaseCallback baseCallback) {
        StaticRankRequest rankRequest = new StaticRankRequest().setChildId(childId).setId(id);
        getCloudManager().submitRequest(context, rankRequest, baseCallback);
    }
}
